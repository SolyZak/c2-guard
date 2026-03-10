package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.Severity;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.enums.TriggerCode;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDecimalDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckListDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckNumberDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckTextDTO;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskPatrolExecutionRepository;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
// ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
// ACL interfaces from task_management module.
// CLEANUP: TaskPresenter and TaskExecutionPresenter stay permanently after Phase E.
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskCheckComparisonPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.DecimalCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.ListCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.NumberCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TextCheckValue;
// ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ExecuteDistributedTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.TodayTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskExecutionSlotEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTasksResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.ImmediateTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.mappers.TaskDistributionMapper;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.TodayTaskSlotProjection;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.DistributedTaskService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DistributedTaskServiceImpl implements DistributedTaskService {
    private final CustomerRepository customerRepository;
    private final TaskExecutionSlotRepository taskExecutionSlotRepository;
    private final TaskPatrolExecutionRepository taskPatrolExecutionRepository;
    private final TaskDistributionMapper taskDistributionMapper;
    private final AttendanceFeignClient attendanceClient;
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // ACL ports injected via @RequiredArgsConstructor.
    // CLEANUP: both fields stay permanently after Phase E.
    private final TaskPresenter taskPresenter;
    private final TaskExecutionPresenter taskExecutionPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
    // ─── [TASK-MIGRATION] NEW: check violation alerts ─────────────────────────────
    // CLEANUP: stays permanently after Phase E.
    private final TriggerRepository triggerRepository;
    private final CrmTriggerLogService crmTriggerLogService;
    private final C2AlertEventService c2AlertEventService;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    @Builder
    private record LocationPoints(BigDecimal longitude, BigDecimal latitude, Long siteId, Long locationId) {}

    @Override
    @Transactional
    public TodayTasksResponse getTodayTasks(TodayTasksRequest todayTasksRequest) {
        CheckInData checkInData = attendanceClient.checkInData();
        Customer customer = getCustomer(checkInData.getCustomerId());
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime todayMidnight = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<TodayTaskSlotProjection> slots = taskExecutionSlotRepository.findTodayTasks(
            customer.getId(),
            todayTasksRequest.contractId(),
            todayMidnight,
            tomorrowMidnight,
            todayTasksRequest.serviceId(),
            todayTasksRequest.serviceTimeId(),
            todayTasksRequest.slotNumber(),
            checkInData.getWorkforceId()
        );

        List<TodayTaskEntryResponse> tasks = new ArrayList<>();
        Map<Long, List<TodayTaskSlotProjection>> groupedSlots = slots.stream()
                .sorted(Comparator.comparing(TodayTaskSlotProjection::getStartDateTime))
                .collect(Collectors.groupingBy(TodayTaskSlotProjection::getTaskDistributionId));

        groupedSlots.forEach((key, slotsList) -> {
            TodayTaskSlotProjection lastSlot = slotsList.getLast();
            List<TodayTaskExecutionSlotEntryResponse> slotResponses = taskDistributionMapper.toExecutionSlotResponseList(slotsList);
            TodayTaskEntryResponse response = taskDistributionMapper.toTodayTaskEntryResponse(lastSlot, slotResponses);

            // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
            // For new-path distributions, task is null so taskName comes back null from the LEFT JOIN.
            // Enrich the name from the task_management ACL before returning the response.
            // CLEANUP: remove this block after Phase E (task always from task_definition by then).
            if (response.taskName() == null && response.taskDefinitionId() != null) {
                String taskName = taskPresenter.getTaskDefinition(response.taskDefinitionId()).getName();
                response = TodayTaskEntryResponse.builder()
                    .taskId(response.taskDefinitionId())
                    .taskDefinitionId(response.taskDefinitionId())
                    .patrolId(response.patrolId())
                    .premiseId(response.premiseId())
                    .locationId(response.locationId())
                    .taskName(taskName)
                    .patrolName(response.patrolName())
                    .locationName(response.locationName())
                    .premiseName(response.premiseName())
                    .patrolFrequency(response.patrolFrequency())
                    .patrolFrequencyRate(response.patrolFrequencyRate())
                    .endDateTime(response.endDateTime())
                    .accessType(response.accessType())
                    .latitude(response.latitude())
                    .longitude(response.longitude())
                    .taskDistributionId(response.taskDistributionId())
                    .distributionType(response.distributionType())
                    .patrolDistributionId(response.patrolDistributionId())
                    .immediateDistributionId(response.immediateDistributionId())
                    .executionSlots(response.executionSlots())
                    .build();
            }
            // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────

            tasks.add(response);
        });
        return TodayTasksResponse.builder().tasks(tasks).build();
    }

    @Override
    @Transactional
    public void executeTask(ExecuteDistributedTaskRequest request, List<MultipartFile> images) {
        CheckInData checkInData = attendanceClient.checkInData();
        Customer customer = getCustomer(checkInData.getCustomerId());
        TaskExecutionSlot taskExecutionSlot = getTaskExecutionSlot(request.executionSlotId());

        // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
        // New path: slot belongs to a distribution that uses task_management.
        // Creates a TaskExecution + TaskCheckExecution via the ACL presenter.
        // CLEANUP: after Phase E, remove the COEXISTENCE block and keep only this path.
        Task task = taskExecutionSlot.getTaskDistribution().getTask();

        if (task == null) {
            // New-path distribution: task is null, use task_definition_id instead
            Long taskDefinitionId = taskExecutionSlot.getTaskDistribution().getTaskDefinitionId();
            if (taskDefinitionId == null) {
                throw new BusinessException(
                        "No task or task definition found for this distribution",
                        HttpStatus.BAD_REQUEST
                );
            }
            executeNewPathTask(request, checkInData, customer, taskExecutionSlot, taskDefinitionId, images);
            return;
        }
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

        // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
        // Old path: slot belongs to a distribution that uses legacy Task entity.
        // Creates TaskPatrolExecution in the old model.
        // CLEANUP: delete this entire block after Phase E.
        checkTaskExecutionConstraints(request, taskExecutionSlot, task);
        TaskPatrolExecution taskPatrolExecution = createTaskPatrolExecution(request, task, customer);
        taskPatrolExecution = taskPatrolExecutionRepository.save(taskPatrolExecution);
        taskExecutionSlot.setStatus(TaskDistributionStatus.FINISHED);
        taskExecutionSlot.setTaskExecution(taskPatrolExecution);
        taskExecutionSlot.setExecutedByWorkforceId(checkInData.getWorkforceId());
        // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────
    }

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Handles execution for new-path distributions (task_management module).
    // Validates check types, creates TaskExecution, and submits each TaskCheckExecution.
    // CLEANUP: rename to the main execution method after Phase E.
    private void executeNewPathTask(
            ExecuteDistributedTaskRequest request,
            CheckInData checkInData,
            Customer customer,
            TaskExecutionSlot taskExecutionSlot,
            Long taskDefinitionId,
            List<MultipartFile> images
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        if (
                taskExecutionSlot.getStatus() != TaskDistributionStatus.CURRENT
                        || now.isBefore(taskExecutionSlot.getStartDateTime())
                        || now.isAfter(taskExecutionSlot.getEndDateTime())
        )
            throw new BusinessException(MessageUtil.getMessage("task.execute.error"), HttpStatus.BAD_REQUEST);

        TaskDefinitionPayload taskDefinition = taskPresenter.getTaskDefinition(taskDefinitionId);
        List<TaskCheckDefinitionPayload> checkDefs = taskDefinition.getChecks();

        if (checkDefs.size() != request.checks().size())
            throw new BusinessException("Task check size not matched", HttpStatus.BAD_REQUEST);

        IntStream.range(0, checkDefs.size()).forEach(i -> {
            String defined   = checkDefs.get(i).getCheckType();
            String submitted = getCheckTypeFromDto(request.checks().get(i));
            if (!defined.equals(submitted))
                throw new BusinessException("Task check type not matched at index " + i, HttpStatus.BAD_REQUEST);
        });

        TaskExecutionPayload taskExecution = taskExecutionPresenter.createTaskExecution(
                CreateTaskExecutionPayload.builder()
                        .workforceId(checkInData.getWorkforceId())
                        .customerId(customer.getId())
                        .build()
        );

        IntStream.range(0, request.checks().size()).forEach(i -> {
            TaskCheckDTO checkDto = request.checks().get(i);
            TaskCheckDefinitionPayload checkDef = checkDefs.get(i);

            // images[i] is the image for checks[i]; null/empty/out-of-bounds → no upload
            MultipartFile file = (images != null && i < images.size()) ? images.get(i) : null;
            String imagePath = (file != null && !file.isEmpty())
                    ? taskExecutionPresenter.uploadCheckExecutionImage(checkDef.getId(), file)
                    : null;


            TaskCheckExecutionPayload checkExecution = taskExecutionPresenter.submitTaskCheckExecution(
                    SubmitTaskCheckExecutionPayload.builder()
                            .taskCheckDefinitionId(checkDef.getId())
                            .taskExecutionId(taskExecution.getId())
                            .checkType(checkDef.getCheckType())
                            .checkValues(toCheckValue(checkDto))
                            .evidenceImagePath(imagePath)
                            .comment(checkDto.getComment())
                            .customerId(customer.getId())
                            .build()
            );

            taskExecutionPresenter.createTaskCheckComparison(
                    CreateTaskCheckComparisonPayload.builder()
                            .taskCheckDefinitionId(checkDef.getId())
                            .taskCheckExecutionId(checkExecution.getId())
                            .customerId(customer.getId())
                            .build()
            );
        });

        taskExecutionSlot.setNewTaskExecutionId(taskExecution.getId());
        taskExecutionSlot.setStatus(TaskDistributionStatus.FINISHED);
        taskExecutionSlot.setExecutedByWorkforceId(checkInData.getWorkforceId());

        // After execution is recorded, evaluate each check for violations and fire alerts if needed
        evaluateAndFireCheckAlerts(checkDefs, request.checks(), taskDefinition.getName(), customer, taskExecutionSlot);
    }

    /**
     * Derives the check-type string used by task_management from the polymorphic DTO class.
     * CLEANUP: remove after Phase E once legacy DTOs are retired.
     */
    private static String getCheckTypeFromDto(TaskCheckDTO dto) {
        if (dto instanceof TaskCheckTextDTO)    return "TEXT";
        if (dto instanceof TaskCheckNumberDTO)  return "NUMBER";
        if (dto instanceof TaskCheckDecimalDTO) return "DECIMAL";
        if (dto instanceof TaskCheckListDTO)    return "LIST";
        throw new BusinessException(
            "Unsupported check DTO type: " + dto.getClass().getSimpleName(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Converts a legacy TaskCheckDTO into the TaskCheckValue used by task_management.
     * CLEANUP: remove after Phase E once legacy DTOs are retired.
     */
    private static TaskCheckValue toCheckValue(TaskCheckDTO dto) {
        if (dto instanceof TaskCheckTextDTO t)    return new TextCheckValue(t.getNotes());
        if (dto instanceof TaskCheckNumberDTO n)  return new NumberCheckValue(n.getUnit(), n.getOperator(), n.getValue());
        if (dto instanceof TaskCheckDecimalDTO d) return new DecimalCheckValue(d.getUnit(), d.getOperator(), d.getValue());
        if (dto instanceof TaskCheckListDTO l)    return new ListCheckValue(l.getListItems(), null);
        throw new BusinessException(
            "Unsupported check DTO type: " + dto.getClass().getSimpleName(), HttpStatus.BAD_REQUEST);
    }

    // ─── [TASK-MIGRATION] NEW: check violation alerts ─────────────────────────────
    // Fires PATROL_TASK_Deviation for each submitted check that violates its defined rule.
    // Only checks with a severity level will trigger an alert.
    // CLEANUP: stays permanently after Phase E.
    private void evaluateAndFireCheckAlerts(
        List<TaskCheckDefinitionPayload> checkDefs,
        List<TaskCheckDTO> submittedChecks,
        String taskName,
        Customer customer,
        TaskExecutionSlot taskExecutionSlot
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        Trigger trigger = triggerRepository.findById(TriggerCode.PATROL_TASK_DEVIATION.getId())
            .orElseThrow(() -> new RuntimeException("Trigger PATROL_TASK_Deviation not found"));

        for (int i = 0; i < checkDefs.size(); i++) {
            TaskCheckDefinitionPayload checkDef = checkDefs.get(i);
            // Checks without a configured severity are excluded from alerting
            if (checkDef.getSeverity() == null) continue;

            TaskCheckValue checkSettings = checkDef.getCheckSettings();
            if (!isCheckViolated(checkSettings, submittedChecks.get(i))) continue;

            String description = taskName + " - " + checkDef.getName();

            Severity severity = Severity.valueOf(checkDef.getSeverity());
            LocationPoints loc = getLocationPoints(taskExecutionSlot.getTaskDistribution());

            TriggerEventDto dto = TriggerEventDto.builder()
                .triggerId(trigger.getId())
                .triggerName(trigger.getCode())
                .operationSiteId(loc.siteId())
                .customerId(customer.getId())
                .longitude(loc.longitude().doubleValue())
                .latitude(loc.latitude().doubleValue())
                .eventTime(now.toOffsetTime())
                .eventDate(now.toLocalDate())
                .servicePlatformName(ServicePlatformEnum.CRM.name())
                .workforceId(taskExecutionSlot.getExecutedByWorkforceId())
                .serviceTriggerEventId(0L)
                .description(description)
                .locationId(loc.locationId())
                .build();

            CrmTriggerLog log = crmTriggerLogService.addNewCrmTriggerLog(dto);
            c2AlertEventService.sendNewC2AlertEventWithOverrideSeverity(log, severity, 6L);
        }
    }

    // Returns true if the submitted value violates the check definition's rule.
    // TEXT checks are excluded — no alert criteria defined for them.
    private boolean isCheckViolated(TaskCheckValue checkSettings, TaskCheckDTO dto) {
        if (checkSettings instanceof ListCheckValue lv) {
            if (lv.getAlertValue() == null) return false;
            List<String> submitted = ((TaskCheckListDTO) dto).getListItems();
            return submitted != null && submitted.contains(lv.getAlertValue());
        }
        if (checkSettings instanceof NumberCheckValue nv) {
            Integer actual = ((TaskCheckNumberDTO) dto).getValue();
            return actual != null && !evaluateOperator(nv.getOperator(), actual.doubleValue(), nv.getValue().doubleValue());
        }
        if (checkSettings instanceof DecimalCheckValue dv) {
            Double actual = ((TaskCheckDecimalDTO) dto).getValue();
            return actual != null && !evaluateOperator(dv.getOperator(), actual, dv.getValue());
        }
        return false;
    }

    // Returns true when the actual value satisfies the operator against the threshold.
    // A false result means the check is violated and an alert should fire.
    private boolean evaluateOperator(String operator, double actual, double threshold) {
        return switch (operator) {
            case "gte" -> actual >= threshold;
            case "lte" -> actual <= threshold;
            case "gt"  -> actual >  threshold;
            case "lt"  -> actual <  threshold;
            case "eq"  -> actual == threshold;
            case "ne"  -> actual != threshold;
            default    -> true; // unknown operator → treat as not violated
        };
    }

    private LocationPoints getLocationPoints(TaskDistribution taskDistribution) {
        if (taskDistribution.getDistributionType() == DistributionType.PATROL) {
            PatrolTaskDistribution ptd = taskDistribution.getPatrolTaskDistribution();
            return LocationPoints.builder()
                .longitude(ptd.getLocation().getLongitude())
                .latitude(ptd.getLocation().getLatitude())
                .siteId(ptd.getServiceTime().getSiteDistribution().getSite().getId())
                .locationId(ptd.getLocation().getId())
                .build();
        }
        ImmediateTaskDistribution itd = taskDistribution.getImmediateTaskDistribution();
        if (itd != null && itd.getLocation() != null) {
            return LocationPoints.builder()
                .longitude(itd.getLocation().getLongitude())
                .latitude(itd.getLocation().getLatitude())
                .siteId(0L)
                .locationId(itd.getLocation().getId())
                .build();
        }
        return LocationPoints.builder()
            .longitude(itd != null ? itd.getLongitude() : BigDecimal.ZERO)
            .latitude(itd != null ? itd.getLatitude() : BigDecimal.ZERO)
            .siteId(0L)
            .locationId(null)
            .build();
    }
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    private static void checkTaskExecutionConstraints(
        ExecuteDistributedTaskRequest executeDistributedTaskRequest,
        TaskExecutionSlot taskExecutionSlot,
        Task task
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        if (
            taskExecutionSlot.getStatus() != TaskDistributionStatus.CURRENT
                || now.isBefore(taskExecutionSlot.getStartDateTime())
                || now.isAfter(taskExecutionSlot.getEndDateTime())
        )
            throw new BusinessException(MessageUtil.getMessage("task.execute.error"), HttpStatus.BAD_REQUEST);

        if (task.getTaskChecks().size() != executeDistributedTaskRequest.checks().size())
            throw new BusinessException("Task check size not matched", HttpStatus.BAD_REQUEST);

        IntStream.range(0, task.getTaskChecks().size())
            .forEach(i -> {
                var expected = executeDistributedTaskRequest.checks().get(i).getClass();
                var actual = task.getTaskChecks().get(i).mapToResponse().getClass();

                if (!actual.equals(expected))
                    throw new BusinessException("Task check type not matched", HttpStatus.BAD_REQUEST);
            });
    }

    private static TaskPatrolExecution createTaskPatrolExecution(
        ExecuteDistributedTaskRequest executeDistributedTaskRequest,
        Task task,
        Customer customer
    ) {
        TaskPatrolExecution taskPatrolExecution = new TaskPatrolExecution();
        taskPatrolExecution.setId(task.getId());
        taskPatrolExecution.setName(task.getName());
        List<TaskCheckPatrolExecution> taskChecksPatrolExecution = createTaskCheckPatrolExecutions(executeDistributedTaskRequest, taskPatrolExecution);
        taskPatrolExecution.setTaskCheckPatrolExecutions(taskChecksPatrolExecution);
        taskPatrolExecution.setCustomer(customer);
        return taskPatrolExecution;
    }

    private static List<TaskCheckPatrolExecution> createTaskCheckPatrolExecutions(
        ExecuteDistributedTaskRequest executeDistributedTaskRequest,
        TaskPatrolExecution taskPatrolExecution
    ) {
        List<TaskCheckPatrolExecution> taskChecksPatrolExecution = new ArrayList<>(executeDistributedTaskRequest.checks().size());
        executeDistributedTaskRequest.checks()
            .forEach(check -> taskChecksPatrolExecution.add(check.mapToExecutionEntity(taskPatrolExecution)));
        return taskChecksPatrolExecution;
    }

    private TaskExecutionSlot getTaskExecutionSlot(Long executionSlotId) {
        return taskExecutionSlotRepository
            .findById(executionSlotId)
            .orElseThrow(() -> new BusinessException("Task execution slot not found", HttpStatus.NOT_FOUND));
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(UserNotProvided::new);
    }
}
