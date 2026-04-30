package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.Severity;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.enums.TriggerCode;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.DecimalCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.ListCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.NumberCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TextCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskCheckComparisonPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ExecuteDistributedTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.SubmittedCheckDTO;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.SubmittedDecimalCheckDTO;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.SubmittedListCheckDTO;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.SubmittedNumberCheckDTO;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.SubmittedTextCheckDTO;
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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedTaskServiceImpl implements DistributedTaskService {
    private final CustomerRepository customerRepository;
    private final TaskExecutionSlotRepository taskExecutionSlotRepository;
    private final TaskDistributionMapper taskDistributionMapper;
    private final AttendanceFeignClient attendanceClient;
    private final TaskPresenter taskPresenter;
    private final TaskExecutionPresenter taskExecutionPresenter;
    private final TriggerRepository triggerRepository;
    private final CrmTriggerLogService crmTriggerLogService;
    private final C2AlertEventService c2AlertEventService;

    @Builder
    private record LocationPoints(
            BigDecimal longitude,
            BigDecimal latitude,
            Long siteId,
            Long locationId
    ) {}

    @Override
    @Transactional
    public TodayTasksResponse getTodayTasks(TodayTasksRequest todayTasksRequest) {
        CheckInData checkInData = attendanceClient.checkInData();
        Customer customer = getCustomer(checkInData.getCustomerId());
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime todayMidnight = now.toLocalDate()
                .atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<TodayTaskSlotProjection> slots =
                taskExecutionSlotRepository.findTodayTasks(
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
                .sorted(Comparator.comparing(
                        TodayTaskSlotProjection::getStartDateTime))
                .collect(Collectors.groupingBy(
                        TodayTaskSlotProjection::getTaskDistributionId));

        groupedSlots.forEach((key, slotsList) -> {
            TodayTaskSlotProjection lastSlot = slotsList.getLast();
            List<TodayTaskExecutionSlotEntryResponse> slotResponses =
                    taskDistributionMapper
                            .toExecutionSlotResponseList(slotsList);
            TodayTaskEntryResponse response =
                    taskDistributionMapper
                            .toTodayTaskEntryResponse(lastSlot, slotResponses);

            if (response.taskName() == null
                    && response.taskDefinitionId() != null) {
                String taskName = taskPresenter
                        .getTaskDefinition(response.taskDefinitionId())
                        .getName();
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
                        .patrolDistributionId(
                                response.patrolDistributionId())
                        .immediateDistributionId(
                                response.immediateDistributionId())
                        .executionSlots(response.executionSlots())
                        .build();
            }

            tasks.add(response);
        });
        return TodayTasksResponse.builder().tasks(tasks).build();
    }

    @Override
    @Transactional
    public void executeTask(
            ExecuteDistributedTaskRequest request,
            List<MultipartFile> images) {
        CheckInData checkInData = attendanceClient.checkInData();
        Customer customer = getCustomer(checkInData.getCustomerId());
        TaskExecutionSlot taskExecutionSlot =
                getTaskExecutionSlot(request.executionSlotId());
        Long taskDefinitionId = taskExecutionSlot
                .getTaskDistribution().getTaskDefinitionId();
        executeNewPathTask(request, checkInData, customer,
                taskExecutionSlot, taskDefinitionId, images);
    }

    private void executeNewPathTask(
            ExecuteDistributedTaskRequest request,
            CheckInData checkInData,
            Customer customer,
            TaskExecutionSlot taskExecutionSlot,
            Long taskDefinitionId,
            List<MultipartFile> images) {

        OffsetDateTime now = OffsetDateTime.now();
        if (taskExecutionSlot.getStatus() != TaskDistributionStatus.CURRENT
                || now.isBefore(taskExecutionSlot.getStartDateTime())
                || now.isAfter(taskExecutionSlot.getEndDateTime()))
            throw new BusinessException(
                    MessageUtil.getMessage("task.execute.error"),
                    HttpStatus.BAD_REQUEST);

        TaskDefinitionPayload taskDefinition =
                taskPresenter.getTaskDefinition(taskDefinitionId);
        List<TaskCheckDefinitionPayload> checkDefs =
                taskDefinition.getChecks();

        if (checkDefs.size() != request.checks().size())
            throw new BusinessException(
                    "Task check size not matched",
                    HttpStatus.BAD_REQUEST);

        IntStream.range(0, checkDefs.size()).forEach(i -> {
            String defined = checkDefs.get(i).getCheckType();
            String submitted = getCheckTypeFromDto(
                    request.checks().get(i));
            if (!defined.equals(submitted))
                throw new BusinessException(
                        "Task check type not matched at index " + i,
                        HttpStatus.BAD_REQUEST);
        });

        LocationPoints loc = getLocationPoints(
                taskExecutionSlot.getTaskDistribution());
        Long locationId = loc.locationId();

        TaskExecutionPayload taskExecution =
                taskExecutionPresenter.createTaskExecution(
                        CreateTaskExecutionPayload.builder()
                                .workforceId(checkInData.getWorkforceId())
                                .customerId(customer.getId())
                                .build()
                );

        IntStream.range(0, request.checks().size()).forEach(i -> {
            SubmittedCheckDTO checkDto = request.checks().get(i);
            TaskCheckDefinitionPayload checkDef = checkDefs.get(i);

            MultipartFile file = (images != null && i < images.size())
                    ? images.get(i) : null;
            String imagePath = (file != null && !file.isEmpty())
                    ? taskExecutionPresenter
                    .uploadCheckExecutionImage(checkDef.getId(), file)
                    : null;

            TaskCheckExecutionPayload checkExecution =
                    taskExecutionPresenter.submitTaskCheckExecution(
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
                            .locationId(locationId)
                            .evidenceImagePath(imagePath)
                            .missingQuality(toImageQualityIssues(
                                    checkDto.getMissingQuality()))
                            .build()
            );
        });

        taskExecutionSlot.setTaskExecutionId(taskExecution.getId());
        taskExecutionSlot.setStatus(TaskDistributionStatus.FINISHED);
        taskExecutionSlot.setExecutedByWorkforceId(
                checkInData.getWorkforceId());

        evaluateAndFireCheckAlerts(
                checkDefs, request.checks(),
                taskDefinition.getName(), customer, taskExecutionSlot);
    }

    // ── Type mapping helpers ─────────────────────────────────────────

    private static String getCheckTypeFromDto(SubmittedCheckDTO dto) {
        if (dto instanceof SubmittedTextCheckDTO) return "TEXT";
        if (dto instanceof SubmittedNumberCheckDTO) return "NUMBER";
        if (dto instanceof SubmittedDecimalCheckDTO) return "DECIMAL";
        if (dto instanceof SubmittedListCheckDTO) return "LIST";
        throw new BusinessException(
                "Unsupported check DTO type: "
                        + dto.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST);
    }

    private static TaskCheckValue toCheckValue(SubmittedCheckDTO dto) {
        if (dto instanceof SubmittedTextCheckDTO t)
            return new TextCheckValue(t.getNotes());
        if (dto instanceof SubmittedNumberCheckDTO n)
            return new NumberCheckValue(
                    n.getUnit(), n.getOperator(), n.getValue());
        if (dto instanceof SubmittedDecimalCheckDTO d)
            return new DecimalCheckValue(
                    d.getUnit(), d.getOperator(), d.getValue());
        if (dto instanceof SubmittedListCheckDTO l)
            return new ListCheckValue(l.getListItems(), null);
        throw new BusinessException(
                "Unsupported check DTO type: "
                        + dto.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST);
    }

    private static List<ImageQualityIssue> toImageQualityIssues(
            List<String> raw) {
        if (raw == null || raw.isEmpty()) return null;
        return raw.stream()
                .map(ImageQualityIssue::valueOf)
                .toList();
    }

    // ── Alert evaluation ─────────────────────────────────────────────

    private void evaluateAndFireCheckAlerts(
            List<TaskCheckDefinitionPayload> checkDefs,
            List<SubmittedCheckDTO> submittedChecks,
            String taskName,
            Customer customer,
            TaskExecutionSlot taskExecutionSlot) {

        OffsetDateTime now = OffsetDateTime.now();
        Trigger trigger = triggerRepository
                .findById(TriggerCode.PATROL_TASK_DEVIATION.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Trigger PATROL_TASK_Deviation not found"));

        LocationPoints loc = getLocationPoints(
                taskExecutionSlot.getTaskDistribution());

        boolean hasCoordinates =
                loc.longitude() != null && loc.latitude() != null;
        if (!hasCoordinates) {
            log.warn("Location {} has null coordinates even after "
                            + "premise fallback — violation alerts will "
                            + "be skipped for this execution",
                    loc.locationId());
        }

        for (int i = 0; i < checkDefs.size(); i++) {
            TaskCheckDefinitionPayload checkDef = checkDefs.get(i);
            if (checkDef.getSeverity() == null) continue;

            TaskCheckValue checkSettings = checkDef.getCheckSettings();
            if (!isCheckViolated(checkSettings, submittedChecks.get(i)))
                continue;

            if (!hasCoordinates) {
                log.warn("Skipping alert for check '{}': location {} "
                                + "has null coordinates",
                        checkDef.getName(), loc.locationId());
                continue;
            }

            String description =
                    taskName + " - " + checkDef.getName();
            Severity severity =
                    Severity.valueOf(checkDef.getSeverity());

            TriggerEventDto dto = TriggerEventDto.builder()
                    .triggerId(trigger.getId())
                    .triggerName(trigger.getCode())
                    .operationSiteId(loc.siteId())
                    .customerId(customer.getId())
                    .longitude(loc.longitude().doubleValue())
                    .latitude(loc.latitude().doubleValue())
                    .eventTime(now.toOffsetTime())
                    .eventDate(now.toLocalDate())
                    .servicePlatformName(
                            ServicePlatformEnum.CRM.name())
                    .workforceId(taskExecutionSlot
                            .getExecutedByWorkforceId())
                    .serviceTriggerEventId(0L)
                    .description(description)
                    .locationId(loc.locationId())
                    .build();

            CrmTriggerLog triggerLog =
                    crmTriggerLogService.addNewCrmTriggerLog(dto);
            c2AlertEventService
                    .sendNewC2AlertEventWithOverrideSeverity(
                            triggerLog, severity, 6L);
        }
    }

    private boolean isCheckViolated(
            TaskCheckValue checkSettings, SubmittedCheckDTO dto) {
        if (checkSettings instanceof ListCheckValue lv) {
            if (lv.getAlertValue() == null) return false;
            List<String> submitted =
                    ((SubmittedListCheckDTO) dto).getListItems();
            return submitted != null
                    && submitted.contains(lv.getAlertValue());
        }
        if (checkSettings instanceof NumberCheckValue nv) {
            Integer actual =
                    ((SubmittedNumberCheckDTO) dto).getValue();
            return actual != null && !evaluateOperator(
                    nv.getOperator(),
                    actual.doubleValue(),
                    nv.getValue().doubleValue());
        }
        if (checkSettings instanceof DecimalCheckValue dv) {
            Double actual =
                    ((SubmittedDecimalCheckDTO) dto).getValue();
            return actual != null && !evaluateOperator(
                    dv.getOperator(), actual, dv.getValue());
        }
        return false;
    }

    private boolean evaluateOperator(
            String operator, double actual, double threshold) {
        return switch (operator) {
            case "gte" -> actual >= threshold;
            case "lte" -> actual <= threshold;
            case "gt" -> actual > threshold;
            case "lt" -> actual < threshold;
            case "eq" -> actual == threshold;
            case "ne" -> actual != threshold;
            default -> true;
        };
    }

    // ── Location resolution ──────────────────────────────────────────

    private LocationPoints getLocationPoints(
            TaskDistribution taskDistribution) {
        if (taskDistribution.getDistributionType()
                == DistributionType.PATROL) {
            PatrolTaskDistribution ptd =
                    taskDistribution.getPatrolTaskDistribution();
            Location location = ptd.getLocation();

            BigDecimal lng = location.getLongitude();
            BigDecimal lat = location.getLatitude();

            if ((lng == null || lat == null)
                    && location.getPremise() != null) {
                Premise premise = location.getPremise();
                if (lng == null) lng = premise.getLongitude();
                if (lat == null) lat = premise.getLatitude();
            }

            return LocationPoints.builder()
                    .longitude(lng)
                    .latitude(lat)
                    .siteId(ptd.getServiceTime()
                            .getSiteDistribution()
                            .getSite().getId())
                    .locationId(location.getId())
                    .build();
        }

        ImmediateTaskDistribution itd =
                taskDistribution.getImmediateTaskDistribution();
        if (itd != null && itd.getLocation() != null) {
            Location location = itd.getLocation();

            BigDecimal lng = location.getLongitude();
            BigDecimal lat = location.getLatitude();

            if ((lng == null || lat == null)
                    && location.getPremise() != null) {
                Premise premise = location.getPremise();
                if (lng == null) lng = premise.getLongitude();
                if (lat == null) lat = premise.getLatitude();
            }

            return LocationPoints.builder()
                    .longitude(lng)
                    .latitude(lat)
                    .siteId(0L)
                    .locationId(location.getId())
                    .build();
        }

        return LocationPoints.builder()
                .longitude(itd != null
                        ? itd.getLongitude() : BigDecimal.ZERO)
                .latitude(itd != null
                        ? itd.getLatitude() : BigDecimal.ZERO)
                .siteId(0L)
                .locationId(null)
                .build();
    }

    // ── Lookups ──────────────────────────────────────────────────────

    private TaskExecutionSlot getTaskExecutionSlot(Long executionSlotId) {
        return taskExecutionSlotRepository
                .findById(executionSlotId)
                .orElseThrow(() -> new BusinessException(
                        "Task execution slot not found",
                        HttpStatus.NOT_FOUND));
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository
                .findById(customerId)
                .orElseThrow(UserNotProvided::new);
    }
}