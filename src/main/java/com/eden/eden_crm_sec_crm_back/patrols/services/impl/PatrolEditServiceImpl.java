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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Patrol patrol = findCurrentPatrol(patrolId);

        // Group PatrolDetails by location, preserving order.
        Map<Long, List<PatrolDetail>> byLocation = patrol.getPatrolDetails().stream()
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
                .validFrom(patrol.getValidFrom())
                .details(locations)
                .build();
    }

    // ===================== PATCH save =====================

    @Override
    @Transactional
    public PatrolEditSaveResponse save(Long patrolId, PatrolEditRequest request) {
        lockService.requireHeldOrAbsent(patrolId);
        UserData caller = utils.getLoggedInUser();
        Patrol oldPatrol = findCurrentPatrol(patrolId);
        Customer customer = oldPatrol.getCustomer();

        // --- 1. Snapshot "before" for audit ---
        PatrolEditResponse before = getForEdit(patrolId);

        // --- 2. Determine if a new version is needed ---
        boolean nameChanged = !oldPatrol.getName().equals(request.getName());
        boolean freqChanged = !oldPatrol.getFrequency().equals(request.getFrequency())
                || !oldPatrol.getFrequencyRate().equals(request.getFrequencyRate());
        boolean tasksChanged = !request.getDetails().isEmpty()
                && request.getDetails().stream().anyMatch(d -> !d.getAddTaskIds().isEmpty() || !d.getRemoveTaskIds().isEmpty());
        boolean locationsChanged = !request.getAddLocations().isEmpty() || !request.getRemoveLocationIds().isEmpty();

        boolean needsNewVersion = freqChanged || tasksChanged || locationsChanged;

        // --- 3. Find active service bindings ---
        List<ContractOperationSiteDistributionPatrol> activeBindings =
                bindingRepository.findActiveBindingsByPatrolId(patrolId, LocalDate.now());

        // --- 4. Per-binding cutoff via attendance ---
        Map<Long, LocalDate> bindingCutoffs = new HashMap<>();
        List<AffectedServiceEntry> affectedServices = new ArrayList<>();
        for (ContractOperationSiteDistributionPatrol binding : activeBindings) {
            PeriodInProgressResponse attResp = attendanceGateway.isPeriodInProgress(
                    PeriodInProgressRequest.builder()
                            .customerId(customer.getId())
                            .contractId(binding.getCustomerContract().getId())
                            .siteId(binding.getSite().getId())
                            .serviceId(binding.getCustomerService().getId())
                            .serviceTimeId(null) // patrol-level; binding doesn't have a specific serviceTimeId
                            .asOfInstant(OffsetDateTime.now())
                            .build());
            boolean inProgress = attResp.isInProgress();
            LocalDate cutoff = inProgress ? LocalDate.now().plusDays(1) : LocalDate.now();
            bindingCutoffs.put(binding.getId(), cutoff);
            affectedServices.add(AffectedServiceEntry.builder()
                    .serviceId(binding.getCustomerService().getId())
                    .cutoffDate(cutoff)
                    .skippedTodayDueToAttendance(inProgress)
                    .build());
        }

        LocalDate globalCutoff = bindingCutoffs.values().stream()
                .max(LocalDate::compareTo).orElse(LocalDate.now());

        Patrol newPatrol;
        if (needsNewVersion) {
            // --- 5. Copy-on-edit: create new Patrol version ---
            newPatrol = new Patrol();
            newPatrol.setName(request.getName());
            newPatrol.setFrequency(request.getFrequency());
            newPatrol.setFrequencyRate(request.getFrequencyRate());
            newPatrol.setCustomer(customer);
            newPatrol.setValidFrom(globalCutoff);
            newPatrol.setValidTo(null);
            newPatrol.setPreviousPatrol(oldPatrol);

            // Build new PatrolDetails from the request.
            List<PatrolDetail> newDetails = buildNewDetails(newPatrol, oldPatrol, request, customer);
            newPatrol.setPatrolDetails(newDetails);

            newPatrol = patrolRepository.saveAndFlush(newPatrol);

            // Close old version.
            oldPatrol.setValidTo(globalCutoff.minusDays(1));
            patrolRepository.save(oldPatrol);

            // --- 6. Per-binding: close old, open new, regenerate slots ---
            for (ContractOperationSiteDistributionPatrol binding : activeBindings) {
                LocalDate cutoff = bindingCutoffs.get(binding.getId());

                // Close old binding.
                binding.setEndDate(cutoff.minusDays(1));
                bindingRepository.save(binding);

                // Create new binding under new patrol.
                ContractOperationSiteDistributionPatrol newBinding = new ContractOperationSiteDistributionPatrol();
                newBinding.setPatrol(newPatrol);
                newBinding.setSite(binding.getSite());
                newBinding.setStartDate(cutoff);
                newBinding.setEndDate(null);
                newBinding.setLocation(binding.getLocation());
                newBinding.setTaskDefinitionId(binding.getTaskDefinitionId());
                newBinding.setCustomerService(binding.getCustomerService());
                newBinding.setCustomerContract(binding.getCustomerContract());
                newBinding.setFromTime(binding.getFromTime());
                newBinding.setToTime(binding.getToTime());
                newBinding.setCustomer(customer);
                newBinding.setPatrolFrequencyType(request.getFrequency());
                newBinding.setStatus(binding.getStatus());
                newBinding.setUniqueId(UUID.randomUUID().toString());
                bindingRepository.save(newBinding);

                // Delete future slots under old patrol for this binding's service.
                deleteFutureSlotsForBinding(oldPatrol.getId(), binding, cutoff);
            }

            // --- 7. Regenerate slots under new patrol ---
            // For each new PatrolDetail, create distributions for each active binding's service/serviceTime.
            // This is complex: each binding has a service + site; we need to find the serviceTime
            // from the SiteDistribution. For now, we rely on the US2 add-task path to be called
            // per-service after the patrol edit — or the FE triggers a re-distribute.
            // The slot cleanup above ensures no duplicates.

        } else {
            // Name-only change — no versioning needed.
            newPatrol = oldPatrol;
            if (nameChanged) {
                oldPatrol.setName(request.getName());
                patrolRepository.save(oldPatrol);
            }
        }

        // --- 8. Audit ---
        UUID editSessionId = UUID.randomUUID();
        PatrolEditResponse after = getForEdit(newPatrol.getId());
        auditService.record(
                editSessionId,
                oldPatrol.getId(),
                newPatrol.getId(),
                customer.getId(),
                caller,
                globalCutoff,
                before,
                after,
                affectedServices
        );

        // --- 9. Release lock ---
        lockService.release(patrolId);

        return PatrolEditSaveResponse.builder()
                .newPatrolId(newPatrol.getId())
                .previousPatrolId(oldPatrol.getId())
                .validFrom(newPatrol.getValidFrom())
                .editSessionId(editSessionId)
                .affectedServices(affectedServices)
                .build();
    }

    // ===================== Helpers =====================

    private Patrol findCurrentPatrol(Long patrolId) {
        Patrol patrol = patrolRepository.findById(patrolId)
                .orElseThrow(() -> new BusinessException("PATROL_NOT_FOUND", HttpStatus.NOT_FOUND));
        if (patrol.getValidTo() != null) {
            throw new BusinessException("PATROL_VERSION_STALE: patrolId " + patrolId
                    + " was superseded on " + patrol.getValidTo(), HttpStatus.CONFLICT);
        }
        return patrol;
    }

    private List<PatrolDetail> buildNewDetails(
            Patrol newPatrol, Patrol oldPatrol, PatrolEditRequest request, Customer customer) {

        List<PatrolDetail> result = new ArrayList<>();
        Set<Long> removeLocationIds = new HashSet<>(request.getRemoveLocationIds());
        Map<Long, PatrolEditRequest.LocationDelta> deltaByLocation = request.getDetails().stream()
                .collect(Collectors.toMap(PatrolEditRequest.LocationDelta::getLocationId, d -> d));

        // Carry over existing details (minus removed locations and removed tasks).
        for (PatrolDetail oldDetail : oldPatrol.getPatrolDetails()) {
            Long locId = oldDetail.getLocation().getId();
            if (removeLocationIds.contains(locId)) continue;

            PatrolEditRequest.LocationDelta delta = deltaByLocation.get(locId);
            Set<Long> removeTaskIds = delta != null ? new HashSet<>(delta.getRemoveTaskIds()) : Set.of();

            if (removeTaskIds.contains(oldDetail.getTaskDefinitionId())) continue;

            PatrolDetail copy = new PatrolDetail();
            copy.setPatrol(newPatrol);
            copy.setLocation(oldDetail.getLocation());
            copy.setTaskDefinitionId(oldDetail.getTaskDefinitionId());
            copy.setDisplayOrder(delta != null && delta.getDisplayOrder() != null
                    ? delta.getDisplayOrder() : oldDetail.getDisplayOrder());
            result.add(copy);
        }

        // Add new tasks to existing locations.
        for (PatrolEditRequest.LocationDelta delta : request.getDetails()) {
            if (removeLocationIds.contains(delta.getLocationId())) continue;
            Location location = locationRepository.findById(delta.getLocationId())
                    .orElseThrow(() -> new BusinessException(
                            "LOCATION_NOT_IN_CUSTOMER_PREMISES: " + delta.getLocationId(),
                            HttpStatus.CONFLICT));
            for (Long taskId : delta.getAddTaskIds()) {
                PatrolDetail pd = new PatrolDetail();
                pd.setPatrol(newPatrol);
                pd.setLocation(location);
                pd.setTaskDefinitionId(taskId);
                pd.setDisplayOrder(delta.getDisplayOrder() != null ? delta.getDisplayOrder() : result.size() + 1);
                result.add(pd);
            }
        }

        // Add new locations with their tasks.
        for (PatrolEditRequest.NewLocation newLoc : request.getAddLocations()) {
            Location location = locationRepository.findById(newLoc.getLocationId())
                    .orElseThrow(() -> new BusinessException(
                            "LOCATION_NOT_IN_CUSTOMER_PREMISES: " + newLoc.getLocationId(),
                            HttpStatus.CONFLICT));
            for (Long taskId : newLoc.getTaskIds()) {
                PatrolDetail pd = new PatrolDetail();
                pd.setPatrol(newPatrol);
                pd.setLocation(location);
                pd.setTaskDefinitionId(taskId);
                pd.setDisplayOrder(newLoc.getDisplayOrder() != null ? newLoc.getDisplayOrder() : result.size() + 1);
                result.add(pd);
            }
        }

        return result;
    }

    /**
     * Deletes future CREATED slots for all PatrolTaskDistributions that belong to
     * the old patrol and are associated with the given binding's service. Cancels
     * their scheduled jobs in one bulk SQL.
     */
    private void deleteFutureSlotsForBinding(Long oldPatrolId,
                                              ContractOperationSiteDistributionPatrol binding,
                                              LocalDate cutoff) {
        // Find all PatrolTaskDistribution rows under this patrol + service + site.
        List<PatrolTaskDistribution> ptds = patrolTaskDistributionRepository
                .findByPatrolAndServiceAndSite(
                        oldPatrolId,
                        binding.getCustomerService().getId(),
                        binding.getSite().getId());

        OffsetDateTime cutoffInstant = cutoff.atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        List<Long> slotIdsToDelete = new ArrayList<>();

        for (PatrolTaskDistribution ptd : ptds) {
            if (ptd.getTaskDistribution().getExecutionSlots() == null) continue;
            List<TaskExecutionSlot> futureSlots = ptd.getTaskDistribution().getExecutionSlots().stream()
                    .filter(s -> s.getStartDateTime() != null
                            && !s.getStartDateTime().isBefore(cutoffInstant)
                            && s.getStatus() == TaskDistributionStatus.CREATED)
                    .toList();
            slotIdsToDelete.addAll(futureSlots.stream().map(TaskExecutionSlot::getId).toList());
        }

        if (!slotIdsToDelete.isEmpty()) {
            flexSchedulerCleanupRepository.deleteJobsForSlotIds(slotIdsToDelete);
            taskExecutionSlotRepository.deleteAllByIdInBatch(slotIdsToDelete);
        }

        // Clean up distributions with no remaining slots.
        for (PatrolTaskDistribution ptd : ptds) {
            if (ptd.getTaskDistribution().getExecutionSlots() == null) continue;
            long remaining = ptd.getTaskDistribution().getExecutionSlots().stream()
                    .filter(s -> !slotIdsToDelete.contains(s.getId()))
                    .count();
            if (remaining == 0) {
                patrolTaskDistributionRepository.delete(ptd);
                taskDistributionRepository.delete(ptd.getTaskDistribution());
            }
        }
    }
}
