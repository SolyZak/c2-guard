package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.rest;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskDefinitionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.LocationTaskDefinitionsResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionSummaryResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskDefinitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/task-definitions")
@RequiredArgsConstructor
public class TaskDefinitionController {

    private final TaskDefinitionService taskDefinitionService;

    @PostMapping
    public ApiResponse<?> createTaskDefinition(@Valid @RequestBody CreateTaskDefinitionRequest request) {
        taskDefinitionService.createTaskDefinition(request);
        return ApiResponse.created();
    }

    @GetMapping
    public ApiResponse<PaginateResponse<TaskDefinitionResponse>> listTaskDefinitions(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size) {
        List<TaskDefinitionResponse> content = taskDefinitionService.listTaskDefinitions(page, size);
        long totalElements = taskDefinitionService.countTaskDefinitions();
        long totalPages = (long) Math.ceil((double) totalElements / size);
        return ApiResponse.ok(new PaginateResponse<>(content, page, size, totalElements, totalPages));
    }

    @GetMapping("/all")
    public ApiResponse<List<TaskDefinitionSummaryResponse>> listAllTaskDefinitions() {
        return ApiResponse.ok(taskDefinitionService.listAllTaskDefinitions());
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskDefinitionResponse> getTaskDefinition(@PathVariable Long id) {
        return ApiResponse.ok(taskDefinitionService.getTaskDefinition(id));
    }

    @GetMapping("/premise/{premiseId}/checks")
    public ApiResponse<List<LocationTaskDefinitionsResponse>> getTaskChecksByPremise(
            @PathVariable Long premiseId) {
        return ApiResponse.ok(taskDefinitionService.getTaskChecksByPremise(premiseId));
    }
}
