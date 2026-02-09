package com.eden.eden_crm_sec_crm_back.taskdistribution.services.base;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CreateScheduledTaskForDistributionService {
    CompletableFuture<Void> createDistributionScheduledTasks(
        String taskDistributionIdFieldName,
        Long taskDistributionId,
        List<TaskExecutionSlot> executionSlots
    );
}
