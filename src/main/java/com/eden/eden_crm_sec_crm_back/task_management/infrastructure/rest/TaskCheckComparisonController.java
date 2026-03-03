package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.rest;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.UpdateTaskCheckComparisonMatchingRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonReportResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskCheckComparisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/task-check-comparisons")
@RequiredArgsConstructor
public class TaskCheckComparisonController {

    private final TaskCheckComparisonService taskCheckComparisonService;

    @GetMapping("/report")
    public ApiResponse<List<TaskCheckComparisonReportResponse>> getComparisonReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<TaskCheckComparisonReportResponse> report = taskCheckComparisonService.getComparisonReport(from, to);
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