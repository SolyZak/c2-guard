package com.eden.eden_crm_sec_crm_back.patrols.controllers;

import com.eden.eden_crm_sec_crm_back.patrols.dtos.request.PatrolReportRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.PatrolReportDetailsResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.PatrolReportResponseDto;
import com.eden.eden_crm_sec_crm_back.patrols.services.base.PatrolService;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/patrols")
public class PatrolController {
    private final PatrolService patrolService;

    @PostMapping("/report")
    ApiResponse<List<PatrolReportResponseDto>> getPatrolReport(@Valid @RequestBody PatrolReportRequest patrolReportRequest) {
        return ApiResponse.ok(patrolService.generatePatrolReport(patrolReportRequest));
    }

    @GetMapping("/report/premise/{premiseId}/patrol/{patrolId}")
    ApiResponse<PatrolReportDetailsResponse> getPatrolReportDetails(@PathVariable Long premiseId, @PathVariable Long patrolId) {
        return ApiResponse.ok(patrolService.getPatrolReportDetails(premiseId, patrolId));
    }
}
