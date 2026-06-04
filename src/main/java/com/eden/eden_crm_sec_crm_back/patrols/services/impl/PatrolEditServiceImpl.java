package com.eden.eden_crm_sec_crm_back.patrols.services.impl;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.clients.AttendanceGateway;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressRequest;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressResponse;
import com.eden.eden_crm_sec_crm_back.locks.services.CustomerEditLockService;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditResponse.PatrolEditLocationEntry;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditResponse.PatrolEditTaskEntry;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditSaveResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditSaveResponse.AffectedServiceEntry;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.patrols.services.PatrolEditService;
import com.eden.eden_crm_sec_crm_back.patrols.services.PatrolVersionAuditService;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PatrolDetailRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.FlexSchedulerCleanupRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.PatrolTaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.TaskDistributionService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * US1: edit a Patrol definition (name, frequency, locations, tasks) <b>in
 * place</b>. There is no copy-on-edit versioning: the patrol row is mutated
 * directly, future task-execution slots are regenerated across every service
 * that uses the patrol (respecting per-service attendance cutoffs), and the
 * edit is recorded in {@code patrol_version_audit} (before/after snapshots plus
 * a field-level {@code changes} delta). Removed tasks/locations that still have
 * execution history are soft-deleted on {@code patrol_detail}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatrolEditServiceImpl implements PatrolEditService {

    private final PatrolRepository patrolRepository;
    private final PatrolDetailRepository patrolDetailRepository;
    private final LocationRepository locationRepository;
    private final ContractOperationSiteDistributionPatrolRepository bindingRepository;
    private final PatrolTaskDistributionRepository patrolTaskDistributionRepository;
    private final TaskDistributionRepository taskDistributionRepository;
    private final TaskExecutionSlotRepository taskExecutionSlotRepository;
    private final FlexSchedulerCleanupRepository flexSchedulerCleanupRepository;
    private final TaskDistributionService taskDistributionService;
    private final PatrolVersionAuditService auditService;
    private final CustomerEditLockService lockService;
    private final AttendanceGateway attendanceGateway;
    private final TaskPresenter taskPresenter;
    private final Utils utils;

    // ===================== GET /edit =====================

    @Override
    @Transactional(readOnly = true)
    public PatrolEditResponse getForEdit(Long patrolId) {
        lockService.requireHeldOrAbsent(patrolId);
        Patrol patrol = findPatrol(patrolId);

        // Group active (non-deleted) PatrolDetails by location, preserving order.
        Map<Long, List<PatrolDetail>> byLocation = patrol.getPatrolDetails().stream()
                .filter(pd -> !pd.isDeleted())
                .collect(Collectors.groupingBy(pd -> pd.getLocation().getId(), LinkedHashMap::new, Collectors.toList()));

        List<PatrolEditLocationEntry> locations = new ArrayList<>();
        for (Map.Entry<Long, List<PatrolDetail>> entry : byLocation.entrySet()) {
            PatrolDetail sample = entry.getValue().get(0);
            Location loc = sample.getLocation();

            List<PatrolEditTaskEntry> tasks = entry.getValue().stream()
                    .map(pd -> {
                        String taskName = null;
                        if (pd.getTaskDefinitionId() != null) {
                            try {
                                TaskDefinitionPayload td = taskPresenter.getTaskDefinition(pd.getTaskDefinitionId());
                                taskName = td.getName();
                            } catch (Exception e) {
                                taskName = "Task #" + pd.getTaskDefinitionId();
                            }
                        }
                        return PatrolEditTaskEntry.builder()
                                .taskDefinitionId(pd.getTaskDefinitionId())
                                .taskName(taskName)
                                .patrolDetailId(pd.getId())
                                .build();
                    })
                    .toList();

            locations.add(PatrolEditLocationEntry.builder()
                    .locationId(loc.getId())
                    .locationName(loc.getName())
                    .premiseName(loc.getPremise() != null ? loc.getPremise().getName() : null)
                    .displayOrder(sample.getDisplayOrder())
                    .tasks(tasks)
                    .build());
        }

        return PatrolEditResponse.builder()
                .patrolId(patrol.getId())
                .name(patrol.getName())
                .frequency(patrol.getFrequency())
                .frequencyRate(patrol.getFrequencyRate())
                .validFrom(patrol.getCreatedDate() != null ? patrol.getCreatedDate().toLocalDate() : null)
                .details(locations)
                .build();
    }

    // ===================== PATCH save (in-place) =====================

    @Override
    @Transactional
    public PatrolEditSaveResponse save(Long patrolId, PatrolEditRequest request) {
        lockService.requireHeldOrAbsent(patrolId);
        UserData caller = utils.getLoggedInUser();
        Patrol patrol = findPatrol(patrolId);
        Customer customer = patrol.getCustomer();

        // --- 1. Snapshot "before" for audit ---
        PatrolEditResponse before = getForEdit(patrolId);

        // --- 2. Change detection ---
        boolean nameChanged = !Objects.equals(patrol.getName(), request.getName());
        boolean freqChanged = !Objects.equals(patrol.getFrequency(), request.getFrequency())
                || !Objects.equals(patrol.getFrequencyRate(), request.getFrequencyRate());
        boolean tasksChanged = request.getDetails().stream()
                .anyMatch(d -> !d.getAddTaskIds().isEmpty() || !d.getRemoveTaskIds().isEmpty());
        boolean locationsChanged = !request.getAddLocations().isEmpty() || !request.getRemoveLocationIds().isEmpty();
        if (!nameChanged && !freqChanged && !tasksChanged && !locationsChanged) {
            throw new BusinessException("NO_CHANGES", HttpStatus.BAD_REQUEST);
        }

        // --- 3. Active service bindings -> per-service attendance cutoff ---
        List<ContractOperationSiteDistributionPatrol> activeBindings =
                bindingRepository.findActiveBindingsByPatrolId(patrolId, LocalDate.now());
        Map<Long, LocalDate> serviceCutoffs = new HashMap<>();
        List<AffectedServiceEntry> affectedServices = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (ContractOperationSiteDistributionPatrol binding : activeBindings) {
            PeriodInProgressResponse attResp = attendanceGateway.isPeriodInProgress(
                    PeriodInProgressRequest.builder()
                            .customerId(customer.getId())
                            .contractId(binding.getCustomerContract().getId())
                            .siteId(binding.getSite().getId())
                            .serviceId(binding.getCustomerService().getId())
                            .serviceTimeId(null) // patrol-level
                            .asOfInstant(OffsetDateTime.now())
                            .build());
            boolean inProgress = attResp.isInProgress();
            LocalDate cutoff = inProgress ? today.plusDays(1) : today;
            serviceCutoffs.merge(binding.getCustomerService().getId(), cutoff,
                    (a, b) -> a.isAfter(b) ? a : b);
            affectedServices.add(AffectedServiceEntry.builder()
                    .serviceId(binding.getCustomerService().getId())
                    .cutoffDate(cutoff)
                    .skippedTodayDueToAttendance(inProgress)
                    .build());
        }
        LocalDate globalCutoff = serviceCutoffs.values().stream()
                .max(LocalDate::compareTo).orElse(today);

        // --- 4. Apply definition changes (name / frequency / add details) ---
        patrol.setName(request.getName());
        patrol.setFrequency(request.getFrequency());
        patrol.setFrequencyRate(request.getFrequencyRate());

        List<PatrolDetail> addedDetails = new ArrayList<>();
        for (AddSpec spec : collectAdds(request)) {
            PatrolDetail existing = findDetail(patrol, spec.locationId(), spec.taskId());
            if (existing != null) {
                if (existing.isDeleted()) {
                    existing.setDeleted(false);
                    if (spec.displayOrder() != null) existing.setDisplayOrder(spec.displayOrder());
                    addedDetails.add(existing);
                }
                // else already active -> idempotent no-op
            } else {
                Location location = locationRepository.findById(spec.locationId())
                        .orElseThrow(() -> new BusinessException(
                                "LOCATION_NOT_IN_CUSTOMER_PREMISES: " + spec.locationId(), HttpStatus.CONFLICT));
                PatrolDetail pd = new PatrolDetail();
                pd.setPatrol(patrol);
                pd.setLocation(location);
                pd.setTaskDefinitionId(spec.taskId());
                pd.setDisplayOrder(spec.displayOrder() != null
                        ? spec.displayOrder()
                        : (patrol.getPatrolDetails() != null ? patrol.getPatrolDetails().size() : 0) + 1);
                pd.setDeleted(false);
                patrol.getPatrolDetails().add(pd);
                addedDetails.add(pd);
            }
        }
        applyReorder(patrol, request);

        // Flush so new details get ids before distributions reference them.
        patrolRepository.saveAndFlush(patrol);

        // --- 5. Load all distributions for this patrol (all services/serviceTimes/sites) ---
        List<PatrolTaskDistribution> allRows = patrolTaskDistributionRepository.findByPatrolId(patrolId);
        Set<Long> removeLocationIds = new HashSet<>(request.getRemoveLocationIds());
        Map<Long, Set<Long>> removeTasksByLocation = collectRemoves(request);

        // --- 6. Frequency change: regenerate future slots on non-removed rows ---
        if (freqChanged) {
            for (PatrolTaskDistribution row : allRows) {
                if (isRemoved(row, removeLocationIds, removeTasksByLocation)) continue;
                LocalDate cutoff = serviceCutoffs.getOrDefault(row.getService().getId(), globalCutoff);
                taskDistributionService.regenerateFutureSlots(row, cutoff);
            }
        }

        // --- 7. Remove: cut future slots for removed task/location rows ---
        for (PatrolTaskDistribution row : allRows) {
            if (!isRemoved(row, removeLocationIds, removeTasksByLocation)) continue;
            LocalDate cutoff = serviceCutoffs.getOrDefault(row.getService().getId(), globalCutoff);
            cutFutureSlots(row, cutoff);
        }

        // --- 8. Soft/hard delete removed PatrolDetails ---
        for (PatrolDetail pd : new ArrayList<>(patrol.getPatrolDetails())) {
            if (pd.isDeleted()) continue;
            boolean wholeLoc = removeLocationIds.contains(pd.getLocation().getId());
            boolean perTask = removeTasksByLocation.getOrDefault(pd.getLocation().getId(), Set.of())
                    .contains(pd.getTaskDefinitionId());
            if (!wholeLoc && !perTask) continue;

            if (pd.getId() != null && patrolTaskDistributionRepository.existsByPatrolDetail_Id(pd.getId())) {
                pd.setDeleted(true); // history still references it
            } else {
                patrol.getPatrolDetails().remove(pd); // orphanRemoval hard-deletes
            }
        }

        // --- 9. Add distributions for added details across every service-time context ---
        Map<Long, PatrolTaskDistribution> contextByServiceTime = new LinkedHashMap<>();
        Map<String, PatrolTaskDistribution> rowBySvcTimeDetail = new HashMap<>();
        for (PatrolTaskDistribution row : allRows) {
            contextByServiceTime.putIfAbsent(row.getServiceTime().getId(), row);
            rowBySvcTimeDetail.put(row.getServiceTime().getId() + ":" + row.getPatrolDetail().getId(), row);
        }
        for (PatrolDetail detail : addedDetails) {
            for (PatrolTaskDistribution ctx : contextByServiceTime.values()) {
                LocalDate cutoff = serviceCutoffs.getOrDefault(ctx.getService().getId(), globalCutoff);
                PatrolTaskDistribution existingRow =
                        rowBySvcTimeDetail.get(ctx.getServiceTime().getId() + ":" + detail.getId());
                if (existingRow != null) {
                    // Re-added task that still has a distribution: ensure fresh future
                    // slots (unless step 6 already regenerated it for a frequency change).
                    if (!freqChanged) {
                        taskDistributionService.regenerateFutureSlots(existingRow, cutoff);
                    }
                } else {
                    taskDistributionService.createSinglePatrolDistribution(
                            ctx.getCustomer(),
                            ctx.getService().getCustomerContract(),
                            ctx.getService(),
                            ctx.getServiceTime(),
                            detail,
                            patrol,
                            detail.getTaskDefinitionId(),
                            cutoff);
                }
            }
        }

        patrolRepository.saveAndFlush(patrol);

        // --- 10. Audit ---
        PatrolEditResponse after = getForEdit(patrolId);
        Map<String, Object> changes = buildChanges(nameChanged, freqChanged, before, request,
                removeLocationIds, removeTasksByLocation);
        UUID editSessionId = UUID.randomUUID();
        auditService.record(
                editSessionId,
                patrolId,
                customer.getId(),
                caller,
                globalCutoff,
                before,
                after,
                changes,
                affectedServices
        );

        // --- 11. Release lock + respond (API contract unchanged) ---
        lockService.release(patrolId);

        return PatrolEditSaveResponse.builder()
                .newPatrolId(patrolId)
                .previousPatrolId(patrolId)
                .validFrom(globalCutoff)
                .editSessionId(editSessionId)
                .affectedServices(affectedServices)
                .build();
    }

    // ===================== Helpers =====================

    private Patrol findPatrol(Long patrolId) {
        return patrolRepository.findById(patrolId)
                .orElseThrow(() -> new BusinessException("PATROL_NOT_FOUND", HttpStatus.NOT_FOUND));
    }

    private record AddSpec(Long locationId, Long taskId, Integer displayOrder) {}

    private List<AddSpec> collectAdds(PatrolEditRequest request) {
        List<AddSpec> specs = new ArrayList<>();
        for (PatrolEditRequest.LocationDelta d : request.getDetails()) {
            for (Long taskId : d.getAddTaskIds()) {
                specs.add(new AddSpec(d.getLocationId(), taskId, d.getDisplayOrder()));
            }
        }
        for (PatrolEditRequest.NewLocation nl : request.getAddLocations()) {
            for (Long taskId : nl.getTaskIds()) {
                specs.add(new AddSpec(nl.getLocationId(), taskId, nl.getDisplayOrder()));
            }
        }
        return specs;
    }

    private Map<Long, Set<Long>> collectRemoves(PatrolEditRequest request) {
        Map<Long, Set<Long>> map = new HashMap<>();
        for (PatrolEditRequest.LocationDelta d : request.getDetails()) {
            if (!d.getRemoveTaskIds().isEmpty()) {
                map.computeIfAbsent(d.getLocationId(), k -> new HashSet<>()).addAll(d.getRemoveTaskIds());
            }
        }
        return map;
    }

    private boolean isRemoved(PatrolTaskDistribution row, Set<Long> removeLocationIds,
                              Map<Long, Set<Long>> removeTasksByLocation) {
        Long locId = row.getLocation().getId();
        if (removeLocationIds.contains(locId)) return true;
        return removeTasksByLocation.getOrDefault(locId, Set.of())
                .contains(row.getTaskDistribution().getTaskDefinitionId());
    }

    private PatrolDetail findDetail(Patrol patrol, Long locationId, Long taskId) {
        if (patrol.getPatrolDetails() == null) return null;
        for (PatrolDetail pd : patrol.getPatrolDetails()) {
            if (pd.getLocation().getId().equals(locationId)
                    && Objects.equals(pd.getTaskDefinitionId(), taskId)) {
                return pd;
            }
        }
        return null;
    }

    private void applyReorder(Patrol patrol, PatrolEditRequest request) {
        Map<Long, Integer> orderByLocation = new HashMap<>();
        for (PatrolEditRequest.LocationDelta d : request.getDetails()) {
            if (d.getDisplayOrder() != null) orderByLocation.put(d.getLocationId(), d.getDisplayOrder());
        }
        if (orderByLocation.isEmpty() || patrol.getPatrolDetails() == null) return;
        for (PatrolDetail pd : patrol.getPatrolDetails()) {
            if (pd.isDeleted()) continue;
            Integer order = orderByLocation.get(pd.getLocation().getId());
            if (order != null) pd.setDisplayOrder(order);
        }
    }

    /**
     * Deletes future CREATED slots (and their flex-scheduler jobs) for the row
     * from {@code cutoff}, preserving past/in-progress slots. If no slots remain
     * afterwards, the distribution and its row are removed entirely.
     */
    private void cutFutureSlots(PatrolTaskDistribution row, LocalDate cutoff) {
        OffsetDateTime cutoffInstant = cutoff.atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        List<TaskExecutionSlot> slots = row.getTaskDistribution().getExecutionSlots() == null
                ? List.of() : row.getTaskDistribution().getExecutionSlots();
        List<Long> futureSlotIds = slots.stream()
                .filter(s -> s.getStartDateTime() != null
                        && !s.getStartDateTime().isBefore(cutoffInstant)
                        && s.getStatus() == TaskDistributionStatus.CREATED)
                .map(TaskExecutionSlot::getId)
                .toList();

        if (!futureSlotIds.isEmpty()) {
            flexSchedulerCleanupRepository.deleteJobsForSlotIds(futureSlotIds);
            taskExecutionSlotRepository.deleteAllByIdInBatch(futureSlotIds);
        }

        long remaining = slots.size() - futureSlotIds.size();
        if (remaining <= 0) {
            patrolTaskDistributionRepository.delete(row);
            taskDistributionRepository.delete(row.getTaskDistribution());
        }
    }

    private Map<String, Object> buildChanges(boolean nameChanged, boolean freqChanged,
                                             PatrolEditResponse before, PatrolEditRequest request,
                                             Set<Long> removeLocationIds,
                                             Map<Long, Set<Long>> removeTasksByLocation) {
        Map<String, Object> changes = new LinkedHashMap<>();
        changes.put("nameChanged", nameChanged);
        if (nameChanged) {
            Map<String, Object> name = new LinkedHashMap<>();
            name.put("from", before.getName());
            name.put("to", request.getName());
            changes.put("name", name);
        }
        changes.put("frequencyChanged", freqChanged);
        if (freqChanged) {
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("frequencyFrom", before.getFrequency());
            f.put("frequencyTo", request.getFrequency());
            f.put("frequencyRateFrom", before.getFrequencyRate());
            f.put("frequencyRateTo", request.getFrequencyRate());
            changes.put("frequency", f);
        }

        List<Map<String, Object>> addedTasks = new ArrayList<>();
        for (PatrolEditRequest.LocationDelta d : request.getDetails()) {
            for (Long taskId : d.getAddTaskIds()) addedTasks.add(taskEntry(d.getLocationId(), taskId));
        }
        List<Map<String, Object>> addedLocations = new ArrayList<>();
        for (PatrolEditRequest.NewLocation nl : request.getAddLocations()) {
            Map<String, Object> loc = new LinkedHashMap<>();
            loc.put("locationId", nl.getLocationId());
            loc.put("taskIds", new ArrayList<>(nl.getTaskIds()));
            addedLocations.add(loc);
        }
        List<Map<String, Object>> removedTasks = new ArrayList<>();
        removeTasksByLocation.forEach((loc, tasks) ->
                tasks.forEach(taskId -> removedTasks.add(taskEntry(loc, taskId))));

        changes.put("addedTasks", addedTasks);
        changes.put("addedLocations", addedLocations);
        changes.put("removedTasks", removedTasks);
        changes.put("removedLocationIds", new ArrayList<>(removeLocationIds));
        return changes;
    }

    private static Map<String, Object> taskEntry(Long locationId, Long taskId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("locationId", locationId);
        m.put("taskDefinitionId", taskId);
        return m;
    }
}
