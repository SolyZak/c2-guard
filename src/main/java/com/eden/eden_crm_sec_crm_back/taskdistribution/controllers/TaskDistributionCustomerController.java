package com.eden.eden_crm_sec_crm_back.taskdistribution.controllers;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.AvailableServiceTimesRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.AvailableServiceTimeResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.TaskDistributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/task-distribution")
public class TaskDistributionCustomerController {

    private final TaskDistributionService taskDistributionService;

    @PostMapping("/patrol")
    public ApiResponse<String> distributePatrolTasks(@Valid @RequestBody DistributePatrolTaskRequest distributePatrolTaskRequest) {
        taskDistributionService.distributePatrolTasks(distributePatrolTaskRequest);
        return ApiResponse.ok("Patrol tasks distributed successfully");
    }

    @PostMapping("/immediate")
    public ApiResponse<String> distributeImmediateTasks(@Valid @RequestBody DistributeImmediateTaskRequest distributeImmediateTaskRequest) {
        taskDistributionService.distributeImmediateTasks(distributeImmediateTaskRequest);
        return ApiResponse.ok("Immediate tasks distributed successfully");
    }

    @PostMapping("/available-service-times")
    public ApiResponse<List<AvailableServiceTimeResponse>> getAllAvailableServiceTimes(@Valid @RequestBody AvailableServiceTimesRequest availableServiceTimesRequest) {
        return ApiResponse.ok(taskDistributionService.getAllAvailableServiceTimes(availableServiceTimesRequest));
    }
}
