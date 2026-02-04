package com.eden.eden_crm_sec_crm_back.taskdistribution.controllers;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ExecuteDistributedTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.TodayTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTasksResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.DistributedTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workforce/task-distribution")
public class TaskDistributionWorkforceController {

    private final DistributedTaskService distributedTaskService;

    @PostMapping("/today-tasks")
    public ApiResponse<TodayTasksResponse> getTodayTasks(@Valid @RequestBody TodayTasksRequest todayTasksRequest) {
        return ApiResponse.ok(distributedTaskService.getTodayTasks(todayTasksRequest));
    }

    @PostMapping("/execute")
    public ApiResponse<Map<String, Object>> executeTask(@Valid @RequestBody ExecuteDistributedTaskRequest executeDistributedTaskRequest) {
        distributedTaskService.executeTask(executeDistributedTaskRequest);
        return ApiResponse.created(Map.of());
    }
}
