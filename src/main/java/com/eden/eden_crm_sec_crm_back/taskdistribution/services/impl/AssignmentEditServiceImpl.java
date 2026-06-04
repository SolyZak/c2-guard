package com.eden.eden_crm_sec_crm_back.taskdistribution.services.impl;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.clients.AttendanceGateway;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressRequest;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressResponse;
import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.eden.eden_crm_sec_crm_back.locks.services.CustomerEditLockService;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.repository.PatrolDetailRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentDeltaRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentLookupResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentLookupResponse.AssignmentLocationEntry;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentPatchResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.FlexSchedulerCleanupRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.PatrolTaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.AssignmentEditService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.PatrolAssignmentAuditService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.TaskDistributionService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * US2: per-(service, patrol, period, site) assignment editing.
 * <p>GET projects existing PatrolTaskDistribution rows grouped by location.
 * <p>PATCH validates the request, asks Attendance for cutoff, removes future
 * slots of removed entries, adds new distributions for added entries via
 * {@link TaskDistributionService#createSinglePatrolDistribution}, and
 * records an audit row.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentEditServiceImpl implements AssignmentEditService {

    private final PatrolTaskDistributionRepository patrolTaskDistributionRepository;
    private final TaskExecutionSlotRepository taskExecutionSlotRepository;
    private final TaskDistributionRepository taskDistributionRepository;
    private final FlexSchedulerCleanupRepository flexSchedulerCleanupRepository;
    private final PatrolAssignmentAuditService auditService;
    private final CustomerEditLockService lockService;
    private final AttendanceGateway attendanceGateway;
    private final TaskDistributionService taskDistributionService;
    private final PatrolDetailRepository patrolDetailRepository;
    private final Utils utils;

    // ===================== GET =====================

    @Override
    @Transactional(readOnly = true)
    public AssignmentLookupResponse get(Long serviceId, Long patrolId, Long serviceTimeId, Long siteId) {
        List<PatrolTaskDistribution> rows = patrolTaskDistributionRepository
                .findAssignmentRows(serviceId, patrolId, serviceTimeId, siteId);

        if (rows.isEmpty()) {
            return AssignmentLookupResponse.builder().exists(false).build();
        }

        Map<Long, List<PatrolTaskDistribution>> byLocation = rows.stream()
                .collect(Collectors.groupingBy(p -> p.getLocation().getId(), LinkedHashMap::new, Collectors.toList()));

        List<AssignmentLocationEntry> locations = new ArrayList<>(byLocation.size());
        for (Map.Entry<Long, List<PatrolTaskDistribution>> e : byLocation.entrySet()) {
            PatrolTaskDistribution sample = e.getValue().get(0);
            Set<Long> taskIds = e.getValue().stream()
                    .map(p -> p.getTaskDistribution().getTaskDefinitionId())
                    .collect(Collectors.toCollection(TreeSet::new));
            locations.add(AssignmentLocationEntry.builder()
                    .locationId(sample.getLocation().getId())
                    .locationName(sample.getLocation().getName())
                    .premiseName(sample.getLocation().getPremise() != null
                            ? sample.getLocation().getPremise().getName() : null)
                    .taskIds(new ArrayList<>(taskIds))
                    .build());
        }

        return AssignmentLookupResponse.builder()
                .exists(true)
                .serviceId(serviceId)
                .patrolId(patrolId)
                .serviceTimeId(serviceTimeId)
                .siteId(siteId)
                .startDate(rows.get(0).getCreatedAt() != null ? rows.get(0).getCreatedAt().toLocalDate() : null)
                .locations(locations)
                .build();
    }

    // ===================== PATCH =====================

    @Override
    @Transactional
    public AssignmentPatchResponse applyDelta(AssignmentDeltaRequest req) {
        // 1. Lock guard (per-patrol).
        lockService.requireHeldOrAbsent(req.getPatrolId());
        UserData caller = utils.getLoggedInUser();

        // 2. Validate.
        validateRequest(req);

        // 3. Load current state.
        List<PatrolTaskDistribution> currentRows = patrolTaskDistributionRepository
                .findAssignmentRows(req.getServiceId(), req.getPatrolId(), req.getServiceTimeId(), req.getSiteId());
        Map<Long, Set<Long>> tasksBefore = groupTasks(currentRows);

        // 4. Resolve context objects from any existing row (for Attendance call + add path).
        PatrolTaskDistribution anyRow = currentRows.isEmpty() ? null : currentRows.get(0);
        ZoneId siteTz = resolveSiteTimezone(anyRow);
        OffsetDateTime now = OffsetDateTime.now();

        PeriodInProgressResponse attResp = attendanceGateway.isPeriodInProgress(PeriodInProgressRequest.builder()
                .customerId(caller.getCustomerId())
                .contractId(anyRow != null ? resolveContractId(anyRow) : null)
                .siteId(req.getSiteId())
                .serviceId(req.getServiceId())
                .serviceTimeId(req.getServiceTimeId())
                .asOfInstant(now)
                .build());
        boolean inProgress = attResp.isInProgress();
        LocalDate today = LocalDate.now(siteTz);
        LocalDate cutoffDate = inProgress ? today.plusDays(1) : today;

        // 5. REMOVE phase.
        Set<Long> removeLocationIds = new HashSet<>(req.getRemoveLocationIds());
        List<Map<String, Object>> deltaRemoved = new ArrayList<>();
        for (PatrolTaskDistribution row : currentRows) {
            boolean wholeLocation = removeLocationIds.contains(row.getLocation().getId());
            boolean perTask = req.getPerLocation().stream().anyMatch(pl ->
                    pl.getLocationId().equals(row.getLocation().getId())
                            && pl.getRemoveTaskIds().contains(row.getTaskDistribution().getTaskDefinitionId()));
            if (!wholeLocation && !perTask) continue;

            cutFutureSlots(row, cutoffDate, siteTz);
            deltaRemoved.add(Map.of(
                    "locationId", row.getLocation().getId(),
                    "taskDefinitionId", row.getTaskDistribution().getTaskDefinitionId()
            ));
        }

        // 6. ADD phase — delegate to TaskDistributionService.createSinglePatrolDistribution.
        List<Map<String, Object>> deltaAdded = new ArrayList<>();
        if (anyRow != null) {
            Customer customer = anyRow.getCustomer();
            CustomerContract contract = anyRow.getService().getCustomerContract();
            LKCustomerContractService service = anyRow.getService();
            LKCustomerContractOperationService serviceTime = anyRow.getServiceTime();
            Patrol patrol = anyRow.getPatrolDetail().getPatrol();

            for (AssignmentDeltaRequest.PerLocation pl : req.getPerLocation()) {
                for (Long taskDefId : pl.getAddTaskIds()) {
                    // Resolve the PatrolDetail for THIS specific task at this location.
                    // PatrolDetail is per-task, so (patrol, location) is not unique — we must
                    // match on taskDefinitionId. List + findFirst stays crash-safe even if the
                    // route repeats a task at the same location.
                    PatrolDetail detail = patrolDetailRepository
                            .findByPatrolIdAndLocationIdAndTaskDefinitionIdAndDeletedFalseOrderByDisplayOrderAscIdAsc(
                                    patrol.getId(), pl.getLocationId(), taskDefId)
                            .stream().findFirst()
                            .orElseThrow(() -> new BusinessException(
                                    "TASK_NOT_PATROL_DETAIL: taskDefinitionId " + taskDefId
                                            + " is not an active task at locationId " + pl.getLocationId()
                                            + " of patrolId " + patrol.getId(),
                                    HttpStatus.CONFLICT));

                    taskDistributionService.createSinglePatrolDistribution(
                            customer, contract, service, serviceTime,
                            detail, patrol, taskDefId, cutoffDate);

                    deltaAdded.add(Map.of(
                            "locationId", pl.getLocationId(),
                            "taskDefinitionId", taskDefId
                    ));
                }
            }
        }

        // 7. Re-read state and audit.
        List<PatrolTaskDistribution> rowsAfter = patrolTaskDistributionRepository
                .findAssignmentRows(req.getServiceId(), req.getPatrolId(), req.getServiceTimeId(), req.getSiteId());
        Map<Long, Set<Long>> tasksAfter = groupTasks(rowsAfter);

        UUID editSessionId = UUID.randomUUID();
        auditService.record(
                editSessionId,
                req.getServiceId(), req.getPatrolId(), req.getServiceTimeId(), req.getSiteId(),
                caller, cutoffDate,
                toJsonMap(tasksBefore), toJsonMap(tasksAfter),
                Map.of(
                        "added", deltaAdded,
                        "removed", deltaRemoved,
                        "removedLocations", new ArrayList<>(removeLocationIds)
                )
        );

        // Build response with fresh state.
        AssignmentLookupResponse freshState = get(req.getServiceId(), req.getPatrolId(),
                req.getServiceTimeId(), req.getSiteId());
        return AssignmentPatchResponse.builder()
                .editSessionId(editSessionId)
                .cutoffDate(cutoffDate)
                .skippedTodayDueToAttendance(inProgress)
                .locations(freshState.getLocations())
                .build();
    }

    // ===================== Helpers =====================

    private void cutFutureSlots(PatrolTaskDistribution row, LocalDate cutoffDate, ZoneId siteTz) {
        OffsetDateTime cutoffInstant = cutoffDate.atStartOfDay(siteTz).toOffsetDateTime();

        List<TaskExecutionSlot> futureSlots = row.getTaskDistribution().getExecutionSlots() == null
                ? List.of()
                : row.getTaskDistribution().getExecutionSlots().stream()
                    .filter(s -> s.getStartDateTime() != null
                            && !s.getStartDateTime().isBefore(cutoffInstant)
                            && s.getStatus() == TaskDistributionStatus.CREATED)
                    .toList();

        if (futureSlots.isEmpty()) return;

        List<Long> slotIds = futureSlots.stream().map(TaskExecutionSlot::getId).toList();
        flexSchedulerCleanupRepository.deleteJobsForSlotIds(slotIds);
        taskExecutionSlotRepository.deleteAllByIdInBatch(slotIds);

        long remaining = (row.getTaskDistribution().getExecutionSlots() == null ? 0
                : row.getTaskDistribution().getExecutionSlots().size()) - futureSlots.size();
        if (remaining <= 0) {
            patrolTaskDistributionRepository.delete(row);
            taskDistributionRepository.delete(row.getTaskDistribution());
        }
    }

    private void validateRequest(AssignmentDeltaRequest req) {
        boolean hasAdds = req.getPerLocation().stream().anyMatch(pl -> !pl.getAddTaskIds().isEmpty());
        boolean hasRemoves = req.getPerLocation().stream().anyMatch(pl -> !pl.getRemoveTaskIds().isEmpty());
        boolean hasLocationRemoves = !req.getRemoveLocationIds().isEmpty();
        if (!hasAdds && !hasRemoves && !hasLocationRemoves) {
            throw new BusinessException("NO_CHANGES", HttpStatus.BAD_REQUEST);
        }
        for (AssignmentDeltaRequest.PerLocation pl : req.getPerLocation()) {
            Set<Long> overlap = new HashSet<>(pl.getAddTaskIds());
            overlap.retainAll(pl.getRemoveTaskIds());
            if (!overlap.isEmpty()) {
                throw new BusinessException(
                        "VALIDATION_FAILED: addTaskIds and removeTaskIds overlap at location " + pl.getLocationId(),
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    private Map<Long, Set<Long>> groupTasks(List<PatrolTaskDistribution> rows) {
        Map<Long, Set<Long>> out = new LinkedHashMap<>();
        for (PatrolTaskDistribution row : rows) {
            out.computeIfAbsent(row.getLocation().getId(), k -> new TreeSet<>())
                .add(row.getTaskDistribution().getTaskDefinitionId());
        }
        return out;
    }

    private Map<Long, List<Long>> toJsonMap(Map<Long, Set<Long>> in) {
        Map<Long, List<Long>> out = new LinkedHashMap<>(in.size());
        in.forEach((k, v) -> out.put(k, new ArrayList<>(v)));
        return out;
    }

    private ZoneId resolveSiteTimezone(PatrolTaskDistribution sample) {
        if (sample == null) return ZoneId.systemDefault();
        try {
            CustomerSite site = sample.getServiceTime().getSiteDistribution().getSite();
            return mapTz(site.getTimezone());
        } catch (Exception e) {
            log.debug("Could not resolve site timezone, falling back to system default", e);
            return ZoneId.systemDefault();
        }
    }

    private ZoneId mapTz(CustomTimezone tz) {
        if (tz == null) return ZoneId.systemDefault();
        return switch (tz) {
            case EGYPT -> ZoneId.of("Africa/Cairo");
            case SAUDI_ARABIA -> ZoneId.of("Asia/Riyadh");
            case EMIRATES -> ZoneId.of("Asia/Dubai");
            case UTC -> ZoneId.of("UTC");
        };
    }

    private Long resolveContractId(PatrolTaskDistribution sample) {
        try {
            return sample.getService().getCustomerContract().getId();
        } catch (Exception e) {
            return null;
        }
    }
}
