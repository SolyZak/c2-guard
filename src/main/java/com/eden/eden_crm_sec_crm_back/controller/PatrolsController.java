package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.BulkReorderPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.PatrolReportRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.ReorderPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.PatrolReportService;
import com.eden.eden_crm_sec_crm_back.service.PatrolsService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/patrol")
@RequiredArgsConstructor
public class PatrolsController {

    private final PatrolsService patrolService;
    private final PatrolReportService patrolReportService;
    @PostMapping
    ApiResponse addPatrol(@RequestBody @Valid AddPatrolRequest request) throws IOException, WriterException {
        patrolService.addPatrol(request);
        return ApiResponse.created();
    }

    @GetMapping
    ApiResponse listPatrols(@RequestParam(defaultValue = "0", name = "page") Integer page,
                             @RequestParam(defaultValue = "10", name = "size") Integer size,
                             @RequestParam(required = false, name = "search") String search) throws IOException, WriterException {
        return ApiResponse.ok(patrolService.listPatrol(page, size, search));
    }

    @GetMapping("/all")
    ApiResponse listPatrolsNoPagination() throws IOException, WriterException {
        return ApiResponse.ok(patrolService.listAllPatrols());
    }

    @PatchMapping("/{patrolId}/details/reorder")
    ApiResponse reorderPatrolDetail(@PathVariable Long patrolId,
                                    @RequestBody @Valid ReorderPatrolDetailRequest request) {
        patrolService.reorderPatrolDetail(patrolId, request);
        return ApiResponse.ok(null);
    }

    @PatchMapping("/{patrolId}/details/reorder-bulk")
    ApiResponse bulkReorderPatrolDetails(@PathVariable Long patrolId,
                                         @RequestBody @Valid BulkReorderPatrolDetailRequest request) {
        patrolService.bulkReorderPatrolDetails(patrolId, request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/report")
    ApiResponse getPatrolReport(@Valid @RequestBody PatrolReportRequest patrolReportRequest) {
        return ApiResponse.ok(patrolReportService.generatePatrolReport(patrolReportRequest));
    }

//    @GetMapping("/report/premise/{premiseId}/patrol/{patrolId}")
//    ApiResponse getPatrolReportDetatils(@PathVariable Long premiseId, @PathVariable Long patrolId) {
//        return ApiResponse.ok(patrolReportService.getPatrolReportDetails(premiseId, patrolId));
//    }
}
