package com.eden.eden_crm_sec_crm_back.taskdistribution.tasks;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.enums.Severity;
import com.eden.eden_crm_sec_crm_back.enums.TriggerCode;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
// ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
// ACL interface from task_management module — used to look up task name for new-path distributions.
// CLEANUP: this import stays permanently after Phase E.
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.ImmediateTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedTaskMissedStatusJob implements ScheduledTaskFactory {

    public static final String TASK_TYPE = "DistributedTaskMissedStatus";
    private final TaskExecutionSlotRepository repository;
    private final CrmTriggerLogService crmTriggerLogService;
    private final C2AlertEventService c2AlertEventService;
    private final TriggerRepository triggerRepository;
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // ACL port — fetches task name for new-path distributions.
    // CLEANUP: this field stays permanently after Phase E.
    private final TaskPresenter taskPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
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
        TaskExecutionSlot executionSlot = repository.findById(executionSlotId)
                .orElseThrow(() -> new BusinessException("Execution slot not found", HttpStatus.NOT_FOUND));

        if (executionSlot.getStatus() == TaskDistributionStatus.CURRENT) {
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
        final LocationPoints locationPoints = getLocation(taskDistribution);

        // ─── [TASK-MIGRATION] dual-mode ────────────────────────────────────────────────
        // NEW path: fetch task name and severity from task_management ACL.
        // COEXISTENCE path: read name from the legacy Task entity; severity from AlertTriggerSeverity.
        // CLEANUP: remove COEXISTENCE branch and guard after Phase E.
        final String taskName;
        final Severity taskSeverity;
        if (taskDistribution.getTaskDefinitionId() != null) {
            // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
            TaskDefinitionPayload taskDef = taskPresenter.getTaskDefinition(taskDistribution.getTaskDefinitionId());
            taskName     = taskDef.getName();
            // Convert task_management severity String to legacy CRM Severity enum — values are identical
            taskSeverity = Severity.valueOf(taskDef.getSeverity());
            // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────
        } else {
            // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────
            // CLEANUP: delete this branch after Phase E.
            taskName     = taskDistribution.getTask().getName();
            taskSeverity = null;  // old-path: falls back to AlertTriggerSeverity table
            // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────
        }
        // ─── [TASK-MIGRATION] END dual-mode ───────────────────────────────────────────

        // Build patrol name — used in description to identify which patrol missed the task
        final String patrolName;
        if (taskDistribution.getDistributionType() == DistributionType.PATROL
                && taskDistribution.getPatrolTaskDistribution() != null) {
            patrolName = taskDistribution.getPatrolTaskDistribution()
                    .getPatrolDetail().getPatrol().getName();
        } else {
            patrolName = "";  // Immediate distributions have no patrol name
        }
        // Format: "Evening Patrol - Check Perimeter"; or just task name for immediate distributions
        String description = patrolName.isBlank() ? taskName : patrolName + " - " + taskName;

        Long siteId = 0L;
        if (
            taskDistribution.getDistributionType() == DistributionType.PATROL
            && taskDistribution.getPatrolTaskDistribution() != null
        ) {
            siteId = taskDistribution.getPatrolTaskDistribution().getServiceTime().getSiteDistribution().getSite().getId();
        }

        TriggerEventDto triggerEventDto = TriggerEventDto.builder()
                .triggerId(trigger.getId())
                .triggerName(trigger.getCode())
                .operationSiteId(siteId)
                .customerId(executionSlot.getCustomer().getId())
                .longitude(locationPoints.longitude().doubleValue())
                .latitude(locationPoints.latitude().doubleValue())
                .eventTime(now.toOffsetTime())
                .eventDate(now.toLocalDate())
                .servicePlatformName(ServicePlatformEnum.CRM.name())
                .workforceId(0L)
                .serviceTriggerEventId(0L)
                .description(description)
                .build();
        final CrmTriggerLog crmTriggerLog = crmTriggerLogService.addNewCrmTriggerLog(triggerEventDto);
        if (taskSeverity != null) {
            // New-path: severity from task definition — bypass AlertTriggerSeverity table
            c2AlertEventService.sendNewC2AlertEventWithOverrideSeverity(crmTriggerLog, taskSeverity);
        } else {
            c2AlertEventService.sendNewC2AlertEvent(crmTriggerLog);  // old path
        }
    }

    private LocationPoints getLocation(final TaskDistribution taskDistribution) {
        return switch (taskDistribution.getDistributionType()) {
            case PATROL -> {
                PatrolTaskDistribution patrolTaskDistribution = taskDistribution.getPatrolTaskDistribution();
                if (patrolTaskDistribution == null || patrolTaskDistribution.getLocation() == null) {
                    throw new IllegalArgumentException("Patrol task distribution or location is null");
                }
                yield LocationPoints.builder()
                    .longitude(patrolTaskDistribution.getLocation().getLongitude())
                    .latitude(patrolTaskDistribution.getLocation().getLatitude())
                    .build();
            }
            case IMMEDIATE -> {
                ImmediateTaskDistribution immediateTaskDistribution = taskDistribution.getImmediateTaskDistribution();
                if (immediateTaskDistribution == null) {
                    throw new IllegalArgumentException("Immediate task distribution is null");
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
            default -> throw new IllegalArgumentException(
                "Unsupported distribution type: " + taskDistribution.getDistributionType()
            );
        };
    }
}
