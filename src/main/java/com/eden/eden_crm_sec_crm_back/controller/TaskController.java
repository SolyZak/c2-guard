package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.task.AddTaskDistributionRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.task.AddTaskRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskDto;
import com.eden.eden_crm_sec_crm_back.dto.response.TodayTasksResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    @PostMapping
    public ApiResponse createTask(@Valid @RequestBody AddTaskRequest taskRequest) {
        taskService.addTask(taskRequest);
        return ApiResponse.created();
    }

    @GetMapping
    public ApiResponse<PaginateResponse<TaskCheckDto>> listLoggedInTasks(@RequestParam(defaultValue = "0", name = "page") Integer page,
                                                                         @RequestParam(defaultValue = "10", name = "size") Integer size) {
        return ApiResponse.ok(taskService.listTasks(page, size));
    }

    @GetMapping("/all")
    public ApiResponse<List<TaskDto>> listLoggedInTasksNoPagination() {
        return ApiResponse.ok(taskService.listTasksNoPaginationForLoggedInCustomer());
    }

    @GetMapping("/all/{patrolId}/{locationId}")
    public ApiResponse<List<TaskDto>> listLoggedInTasksNoPagination(@PathVariable("patrolId") Long patrolId, @PathVariable("locationId") Long locationId) {
        return ApiResponse.ok(taskService.listTasksNoPaginationForLoggedInCustomerByLocationIdAndPatrolId(locationId, patrolId));
    }

    @GetMapping("/today/{contractId}/{serviceId}/{siteId}/{periodId}")
    public ApiResponse<TodayTasksResponseDto> getTodayTasks(@PathVariable("contractId") Long contractId, @PathVariable("serviceId") Long serviceId, @PathVariable("siteId") Long siteId, @PathVariable("periodId") String periodId) {
        return ApiResponse.ok(taskService.getTodayTasks(contractId, serviceId, siteId, periodId));
    }
}
