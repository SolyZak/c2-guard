package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.rest;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.SubmitTaskCheckExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TaskExecutionController {

    private final TaskExecutionService taskExecutionService;

    @PostMapping("/task-executions")
    public ApiResponse<TaskExecutionResponse> createTaskExecution(
            @Valid @RequestBody CreateTaskExecutionRequest request) {
        return ApiResponse.ok(taskExecutionService.createTaskExecution(request));
    }

    @PostMapping("/task-check-executions")
    public ApiResponse<TaskCheckExecutionResponse> submitTaskCheckExecution(
            @Valid @RequestBody SubmitTaskCheckExecutionRequest request) {
        return ApiResponse.ok(taskExecutionService.submitTaskCheckExecution(request));
    }
}
