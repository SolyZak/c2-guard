package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.TaskDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskDistributionServiceImpl implements TaskDistributionService {

    @Override
    public void distributePatrolTasks(DistributePatrolTaskRequest distributePatrolTaskRequest) {

    }

    @Override
    public void distributeImmediateTasks(DistributeImmediateTaskRequest distributeImmediateTaskRequest) {

    }
}
