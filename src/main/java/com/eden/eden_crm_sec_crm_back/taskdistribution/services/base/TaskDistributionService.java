package com.eden.eden_crm_sec_crm_back.taskdistribution.services.base;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.AvailableServiceTimesRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.AvailableServiceTimeResponse;

import java.util.List;

public interface TaskDistributionService {
    void distributePatrolTasks(DistributePatrolTaskRequest distributePatrolTaskRequest);
    void distributeImmediateTasks(DistributeImmediateTaskRequest distributeImmediateTaskRequest);
    List<AvailableServiceTimeResponse> getAllAvailableServiceTimes(AvailableServiceTimesRequest availableServiceTimesRequest);
}
