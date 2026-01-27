package com.eden.eden_crm_sec_crm_back.tasks;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.enums.TriggerCode;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github._0xorigin.flexscheduler.base.factories.tasks.base.ScheduledTaskFactory;
import io.github._0xorigin.flexscheduler.utils.JsonNodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskMissedStatusJob implements ScheduledTaskFactory {

    public static final String TASK_TYPE = "TaskMissedStatus";
    private final ContractOperationSiteDistributionPatrolRepository repository;
    private final CrmTriggerLogService crmTriggerLogService;
    private final C2AlertEventService c2AlertEventService;
    private final TriggerRepository triggerRepository;

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        Long patrolDistributionId = JsonNodeUtils.getOptionalLong(arguments, "patrolDistributionId")
                .orElseThrow(() -> new BusinessException("Patrol distribution id is required", HttpStatus.BAD_REQUEST));

        Optional<ContractOperationSiteDistributionPatrol> optionalDistribution = repository.findById(patrolDistributionId);
        if (optionalDistribution.isEmpty())
            throw new BusinessException("Patrol distribution not found", HttpStatus.NOT_FOUND);

        ContractOperationSiteDistributionPatrol distribution = optionalDistribution.get();
        if (!distribution.getStatus().equals(TaskDistributionStatus.FINISHED.name())) {
            distribution.setStatus(TaskDistributionStatus.MISSED.name());
            repository.saveAndFlush(distribution);
            sendAlertEvent(distribution);
        }

        ObjectNode result = JsonNodeUtils.createObjectNode();
        result.put("status", "success");
        result.set("usedIds", JsonNodeUtils.createObjectNode().put("patrolDistributionId", patrolDistributionId));
        return result;
    }

    private void sendAlertEvent(ContractOperationSiteDistributionPatrol distribution) {
        OffsetDateTime now = OffsetDateTime.now();
        final Trigger trigger = triggerRepository.findById(TriggerCode.PATROL_TASK_MISSED.getId())
                .orElseThrow(() -> new RuntimeException("Trigger not found"));
        final Task task = distribution.getTask();
        TriggerEventDto triggerEventDto = TriggerEventDto.builder()
                .triggerId(trigger.getId())
                .triggerName(trigger.getCode())
                .operationSiteId(distribution.getSite().getId())
                .customerId(distribution.getCustomer().getId())
                .longitude(distribution.getSite().getLongitude())
                .latitude(distribution.getSite().getLatitude())
                .eventTime(now.toOffsetTime())
                .eventDate(now.toLocalDate())
                .servicePlatformName(ServicePlatformEnum.CRM.name())
                .workforceId(0L)
                .serviceTriggerEventId(0L)
                .description(
                    task.getName() + " | " + distribution.getFromTime() + " - " + distribution.getToTime()
                )
                .build();
        final CrmTriggerLog crmTriggerLog = crmTriggerLogService.addNewCrmTriggerLog(triggerEventDto);
        c2AlertEventService.sendNewC2AlertEvent(crmTriggerLog);
    }
}
