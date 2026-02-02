package com.eden.eden_crm_sec_crm_back.taskdistribution.services.base;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;

public interface TaskDistributionService {
    void distributePatrolTasks(DistributePatrolTaskRequest distributePatrolTaskRequest);
    void distributeImmediateTasks(DistributeImmediateTaskRequest distributeImmediateTaskRequest);
}
