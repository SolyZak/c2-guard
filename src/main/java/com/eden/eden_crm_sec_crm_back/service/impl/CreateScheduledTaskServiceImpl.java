package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.DateTimeScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.services.TaskSchedulerService;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.tasks.TaskCurrentStatusJob;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.tasks.TaskMissedStatusJob;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.service.CreateScheduledTaskService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class CreateScheduledTaskServiceImpl implements CreateScheduledTaskService {
    private final TaskMissedStatusJob taskMissedStatusJob;
    private final TaskCurrentStatusJob taskCurrentStatusJob;
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
                    OffsetDateTime taskStartDateTime = distributionForPatrol.getStartDate().atTime(distributionForPatrol.getFromTime());
                    String taskName = "TaskCurrentStatus - Patrol distribution id: %s, Contract id: %s, Service id: %s"
                            .formatted(distributionForPatrol.getId(), contractId, serviceId);
                    ObjectNode taskParams = JsonNodeFactory.instance.objectNode();
                    taskParams.put("patrolDistributionId", distributionForPatrol.getId());
                    taskParams.put("contractId", contractId);
                    taskParams.put("serviceId", serviceId);
                    return DateTimeScheduledTaskRequest.builder()
                            .name(taskName)
                            .plannedExecutionTime(taskStartDateTime)
                            .arguments(taskParams)
                            .build();
                }).toList();

        List<DateTimeScheduledTaskRequest> scheduledTaskCurrentRequests = distributionForPatrols
                .stream()
                .map(distributionForPatrol -> {
                    OffsetDateTime taskEndDateTime = distributionForPatrol.getEndDate().atTime(distributionForPatrol.getToTime());
                    String taskName = "TaskMissedStatus - Patrol distribution id: %s, Contract id: %s, Service id: %s"
                            .formatted(distributionForPatrol.getId(), contractId, serviceId);
                    ObjectNode taskParams = JsonNodeFactory.instance.objectNode();
                    taskParams.put("patrolDistributionId", distributionForPatrol.getId());
                    taskParams.put("contractId", contractId);
                    taskParams.put("serviceId", serviceId);
                    return DateTimeScheduledTaskRequest.builder()
                            .name(taskName)
                            .plannedExecutionTime(taskEndDateTime)
                            .arguments(taskParams)
                            .build();
                }).toList();

        List<ScheduledTaskEntity> tasksList = taskMissedStatusJob.createTasks(scheduledTaskMissedRequests);
        List<ScheduledTaskEntity> tasksListCurrent = taskCurrentStatusJob.createTasks(scheduledTaskCurrentRequests);
        tasksList.addAll(tasksListCurrent);
//        try {
//            Thread.sleep(40000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        taskSchedulerService.scheduleTasksIfExecuteToday(tasksList);
        return CompletableFuture.completedFuture(null);
    }
}
