package com.eden.eden_crm_sec_crm_back.taskdistribution.tasks;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.enums.TriggerCode;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.ImmediateTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github._0xorigin.flexscheduler.base.factories.tasks.base.ScheduledTaskFactory;
import io.github._0xorigin.flexscheduler.utils.JsonNodeUtils;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedTaskMissedStatusJob implements ScheduledTaskFactory {

    public static final String TASK_TYPE = "DistributedTaskMissedStatus";
    private final TaskExecutionSlotRepository repository;
    private final CrmTriggerLogService crmTriggerLogService;
    private final C2AlertEventService c2AlertEventService;
    private final TriggerRepository triggerRepository;
    @Builder
    private record LocationPoints(BigDecimal longitude, BigDecimal latitude) {}

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        Long executionSlotId = JsonNodeUtils.getOptionalLong(arguments, "executionSlotId")
            .orElseThrow(() -> new BusinessException("Execution slot id is required", HttpStatus.BAD_REQUEST));

        Optional<TaskExecutionSlot> executionSlotOptional = repository.findById(executionSlotId);
        if (executionSlotOptional.isEmpty())
            throw new BusinessException("Execution slot not found", HttpStatus.NOT_FOUND);

        TaskExecutionSlot executionSlot = executionSlotOptional.get();
        if (executionSlot.getStatus() != TaskDistributionStatus.FINISHED) {
            executionSlot.setStatus(TaskDistributionStatus.MISSED);
            repository.saveAndFlush(executionSlot);
            sendAlertEvent(executionSlot);
        }

        ObjectNode result = JsonNodeUtils.createObjectNode();
        result.put("status", "success");
        return result;
    }

    private void sendAlertEvent(TaskExecutionSlot executionSlot) {
        OffsetDateTime now = OffsetDateTime.now();
        final Trigger trigger = triggerRepository.findById(TriggerCode.PATROL_TASK_MISSED.getId())
                .orElseThrow(() -> new RuntimeException("Trigger not found"));
        final TaskDistribution taskDistribution = executionSlot.getTaskDistribution();
        final Task task = taskDistribution.getTask();
        final LocationPoints locationPoints = getLocation(taskDistribution);

        TriggerEventDto triggerEventDto = TriggerEventDto.builder()
                .triggerId(trigger.getId())
                .triggerName(trigger.getCode())
                .operationSiteId(0L)
                .customerId(executionSlot.getCustomer().getId())
                .longitude(locationPoints.longitude().doubleValue())
                .latitude(locationPoints.latitude().doubleValue())
                .eventTime(now.toOffsetTime())
                .eventDate(now.toLocalDate())
                .servicePlatformName(ServicePlatformEnum.CRM.name())
                .workforceId(0L)
                .serviceTriggerEventId(0L)
                .description(
                    task.getName() + " | " + executionSlot.getStartDateTime() + " - " + executionSlot.getEndDateTime()
                )
                .build();
        final CrmTriggerLog crmTriggerLog = crmTriggerLogService.addNewCrmTriggerLog(triggerEventDto);
        c2AlertEventService.sendNewC2AlertEvent(crmTriggerLog);
    }

    private LocationPoints getLocation(final TaskDistribution taskDistribution) {
        return switch (taskDistribution.getDistributionType()) {
            case PATROL -> {
                PatrolTaskDistribution patrolTaskDistribution = taskDistribution.getPatrolTaskDistribution();
                if (patrolTaskDistribution == null || patrolTaskDistribution.getLocation() == null) {
                    throw new RuntimeException("Patrol task distribution or location is null");
                }
                yield LocationPoints.builder()
                    .longitude(patrolTaskDistribution.getLocation().getLongitude())
                    .latitude(patrolTaskDistribution.getLocation().getLatitude())
                    .build();
            }
            case IMMEDIATE -> {
                ImmediateTaskDistribution immediateTaskDistribution = taskDistribution.getImmediateTaskDistribution();
                if (immediateTaskDistribution == null) {
                    throw new RuntimeException("Immediate task distribution is null");
                }
                if (immediateTaskDistribution.getLocation() != null) {
                    yield LocationPoints.builder()
                        .longitude(immediateTaskDistribution.getLocation().getLongitude())
                        .latitude(immediateTaskDistribution.getLocation().getLatitude())
                        .build();
                }
                yield LocationPoints.builder()
                    .longitude(immediateTaskDistribution.getLongitude())
                    .latitude(immediateTaskDistribution.getLatitude())
                    .build();
            }
            default -> throw new RuntimeException(
                "Unsupported distribution type: " + taskDistribution.getDistributionType()
            );
        };
    }
}
