package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.service.CreateScheduledTaskService;
import com.eden.eden_crm_sec_crm_back.tasks.TaskCurrentStatusJob;
import com.eden.eden_crm_sec_crm_back.tasks.TaskMissedStatusJob;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github._0xorigin.flexscheduler.base.dtos.DateTimeScheduledTaskRequest;
import io.github._0xorigin.flexscheduler.services.base.TaskSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CreateScheduledTaskServiceImpl implements CreateScheduledTaskService {
    private final TaskSchedulerService taskSchedulerService;

    @Async
    public CompletableFuture<Void> createDistributionScheduledTasks(
            List<ContractOperationSiteDistributionPatrol> distributionForPatrols,
            Long contractId,
            Long serviceId
    ) {
        List<DateTimeScheduledTaskRequest> scheduledTaskMissedRequests = distributionForPatrols
                .stream()
                .map(distributionForPatrol -> {
                    OffsetDateTime taskStartDateTime = distributionForPatrol.getEndDate().atTime(distributionForPatrol.getToTime());
                    String taskName = "Patrol distribution id: %s, Contract id: %s, Service id: %s"
                            .formatted(distributionForPatrol.getId(), contractId, serviceId);
                    ObjectNode taskParams = JsonNodeFactory.instance.objectNode();
                    taskParams.put("patrolDistributionId", distributionForPatrol.getId());
                    taskParams.put("contractId", contractId);
                    taskParams.put("serviceId", serviceId);
                    return DateTimeScheduledTaskRequest.builder()
                            .name(taskName)
                            .plannedExecutionTime(taskStartDateTime)
                            .arguments(taskParams)
                            .taskType(TaskMissedStatusJob.TASK_TYPE)
                            .build();
                }).toList();

        List<DateTimeScheduledTaskRequest> scheduledTaskCurrentRequests = distributionForPatrols
                .stream()
                .map(distributionForPatrol -> {
                    OffsetDateTime taskEndDateTime = distributionForPatrol.getStartDate().atTime(distributionForPatrol.getFromTime());
                    String taskName = "Patrol distribution id: %s, Contract id: %s, Service id: %s"
                            .formatted(distributionForPatrol.getId(), contractId, serviceId);
                    ObjectNode taskParams = JsonNodeFactory.instance.objectNode();
                    taskParams.put("patrolDistributionId", distributionForPatrol.getId());
                    taskParams.put("contractId", contractId);
                    taskParams.put("serviceId", serviceId);
                    return DateTimeScheduledTaskRequest.builder()
                            .name(taskName)
                            .plannedExecutionTime(taskEndDateTime)
                            .arguments(taskParams)
                            .taskType(TaskCurrentStatusJob.TASK_TYPE)
                            .build();
                }).toList();

        taskSchedulerService.createTasksAndSchedule(scheduledTaskCurrentRequests);
        taskSchedulerService.createTasksAndSchedule(scheduledTaskMissedRequests);
        return CompletableFuture.completedFuture(null);
    }
}
