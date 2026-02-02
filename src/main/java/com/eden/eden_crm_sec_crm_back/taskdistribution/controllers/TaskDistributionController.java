package com.eden.eden_crm_sec_crm_back.taskdistribution.controllers;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task-distribution")
public class TaskDistributionController {

    @PostMapping("/patrol")
    public ApiResponse<String> distributePatrolTasks(@Valid @RequestBody DistributePatrolTaskRequest distributePatrolTaskRequest) {
        return ApiResponse.ok("Patrol tasks distributed successfully");
    }

    @PostMapping("/immediate")
    public ApiResponse<String> distributeImmediateTasks(@Valid @RequestBody DistributeImmediateTaskRequest distributeImmediateTaskRequest) {
        return ApiResponse.ok("Immediate tasks distributed successfully");
    }
}
