package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.rest;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.SubmitTaskCheckExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionCountResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskExecutionService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Task Execution", description = "APIs for task execution management and statistics")
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

    @GetMapping("/workforce/stats/tasks/today-count")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get today's performed tasks count",
            description = "Returns the number of tasks performed (executed) by the logged-in workforce member for the current day."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Performed tasks count retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – workforce token missing or invalid"
            )
    })
    public ApiResponse<TaskExecutionCountResponse> getTodayPerformedTasksCount() {
        Long workforceId = Long.valueOf(Utils.getLoggedInWorkforce().getId());
        return ApiResponse.ok(taskExecutionService.getPerformedTasksCountToday(workforceId));
    }

    @GetMapping("/workforce/stats/tasks/today-count-only")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get today's performed tasks count (raw number)",
            description = "Returns just the count (as a Long) of tasks performed by the logged-in workforce member today."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Performed tasks count retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized – workforce token missing or invalid"
            )
    })
    public ApiResponse<Long> getTodayPerformedTasksCountRaw() {
        Long workforceId = Long.valueOf(Utils.getLoggedInWorkforce().getId());
        return ApiResponse.ok(taskExecutionService.countPerformedTasksToday(workforceId));
    }
}