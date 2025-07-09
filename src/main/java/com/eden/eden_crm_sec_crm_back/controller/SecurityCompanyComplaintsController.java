package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.response.Complaint;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/security-company/complaints")
@RequiredArgsConstructor
@Tag(
        name = "Security Company Complaints APIs",
        description = "This part is for security company portal, will provide the needed for complaints APIs")
public class SecurityCompanyComplaintsController {

    private final ComplaintService complaintService;

    @Operation(summary = "Paginate complaints API with filter criteria")
    @GetMapping
    ApiResponse<PaginateResponse<Complaint>> paginate(
            @RequestParam(name = "customerId", required = false) List<Long> customerId,
            @RequestParam(name = "contractId", required = false) List<Long> contractId,
            @RequestParam(name = "operationSiteId", required = false) List<Long> operationSiteId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return ApiResponse.ok(complaintService.getComplaintsForSecurityCompany(customerId, contractId, operationSiteId, page, size));
    }
}
