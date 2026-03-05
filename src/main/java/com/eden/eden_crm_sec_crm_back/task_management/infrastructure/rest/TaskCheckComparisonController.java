package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.rest;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.UpdateTaskCheckComparisonMatchingRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonReportResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskCheckComparisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/task-check-comparisons")
@RequiredArgsConstructor
public class TaskCheckComparisonController {

    private final TaskCheckComparisonService taskCheckComparisonService;

    @GetMapping("/report")
    public ApiResponse<PaginateResponse<TaskCheckComparisonReportResponse>> getComparisonReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "comparisonDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        PaginateResponse<TaskCheckComparisonReportResponse> report =
                taskCheckComparisonService.getComparisonReport(from, to, page, size, sortBy, sortDirection);
        return ApiResponse.ok(report);
    }

    @PutMapping("/{id}/matching")
    public ApiResponse<TaskCheckComparisonResponse> updateMatching(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskCheckComparisonMatchingRequest request) {
        TaskCheckComparisonResponse response = taskCheckComparisonService.updateMatching(id, request);
        return ApiResponse.ok(response);
    }
}