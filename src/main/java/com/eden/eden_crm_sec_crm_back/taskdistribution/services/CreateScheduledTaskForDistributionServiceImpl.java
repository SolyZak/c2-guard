package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.CreateScheduledTaskForDistributionService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.tasks.DistributedTaskCurrentStatusJob;
import com.eden.eden_crm_sec_crm_back.taskdistribution.tasks.DistributedTaskMissedStatusJob;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github._0xorigin.flexscheduler.base.dtos.DateTimeScheduledTaskRequest;
import io.github._0xorigin.flexscheduler.base.entities.ScheduledTaskEntity;
import io.github._0xorigin.flexscheduler.services.base.TaskSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CreateScheduledTaskForDistributionServiceImpl implements CreateScheduledTaskForDistributionService {
    private final TaskSchedulerService taskSchedulerService;

    @Async
    @Transactional
    public CompletableFuture<Void> createDistributionScheduledTasks(
        String taskDistributionIdFieldName,
        Long taskDistributionId,
        List<TaskExecutionSlot> executionSlots
    ) {
        List<DateTimeScheduledTaskRequest> scheduledTaskMissedRequests = executionSlots
            .stream()
            .map(executionSlot -> {
                OffsetDateTime taskStartDateTime = executionSlot.getEndDateTime();
                String taskName = "Execution slot id: %s".formatted(executionSlot.getId());
                ObjectNode taskParams = JsonNodeFactory.instance.objectNode();
                taskParams.put("executionSlotId", executionSlot.getId());
                taskParams.put(taskDistributionIdFieldName, taskDistributionId);
                return DateTimeScheduledTaskRequest.builder()
                    .name(taskName)
                    .plannedExecutionTime(taskStartDateTime)
                    .arguments(taskParams)
                    .taskType(DistributedTaskMissedStatusJob.TASK_TYPE)
                    .isActive(true)
                    .build();
            }).toList();

        List<DateTimeScheduledTaskRequest> scheduledTaskCurrentRequests = executionSlots
            .stream()
            .map(executionSlot -> {
                OffsetDateTime taskEndDateTime = executionSlot.getStartDateTime();
                String taskName = "Execution slot id: %s".formatted(executionSlot.getId());
                ObjectNode taskParams = JsonNodeFactory.instance.objectNode();
                taskParams.put("executionSlotId", executionSlot.getId());
                taskParams.put(taskDistributionIdFieldName, taskDistributionId);
                return DateTimeScheduledTaskRequest.builder()
                    .name(taskName)
                    .plannedExecutionTime(taskEndDateTime)
                    .arguments(taskParams)
                    .taskType(DistributedTaskCurrentStatusJob.TASK_TYPE)
                    .isActive(true)
                    .build();
            }).toList();

        List<ScheduledTaskEntity> tasks = taskSchedulerService.createTasksInstances(scheduledTaskCurrentRequests);
        tasks.addAll(taskSchedulerService.createTasksInstances(scheduledTaskMissedRequests));

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    taskSchedulerService.scheduleTasksIfExecuteToday(tasks);
                }
            });
        } else {
            taskSchedulerService.scheduleTasksIfExecuteToday(tasks);
        }

        return CompletableFuture.completedFuture(null);
    }
}
