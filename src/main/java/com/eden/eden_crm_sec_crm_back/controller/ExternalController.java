package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.external.*;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.service.ExternalService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/external")
@RequiredArgsConstructor
@Tag(
        name = "External, Service To Service APIs",
        description = "This part is for service to service usage, will provide the needed from crm service to other services")
public class ExternalController {
    private final ExternalService externalService;
    @Operation(summary = "Get operation site info for today", description = "This API will provide information abut operation site today status")
    @GetMapping("/operation-sites/{id}")
    public OperationSiteInfo getOperationSiteDetails(@PathVariable(name = "id") Long id) {
        return externalService.getOperationSiteDetails(id);
    }

    @Operation(summary = "Get all operation sites for a customer")
    @GetMapping("/operation-sites/{id}/all")
    public List<OperationSiteData> getCustomerOperationSites(@PathVariable(name = "id") Long customerId) {
        return externalService.getCustomerOperationSites(customerId);
    }

    @Operation(summary = "Get customer info", description = "This API will provide information abut customer")
    @GetMapping("/customers/{id}")
    public CustomerInfo getCustomerInfo(@PathVariable(name = "id") Long id) {
        return externalService.getCustomerInfo(id);
    }

    @Operation(summary = "Get operation site distributions for the logged in workforce")
    @GetMapping("/workforce/operation-sites/{id}/distributions")
    public WorkforceSiteDistributionDto operationSiteServicesDropdown(
            @PathVariable("id") Long id
    ) {
        return externalService.operationSiteServicesDropdown(id);
    }

    @Operation(summary = "Get customer attendance stats for operation site")
    @GetMapping("/customer/attendance/stats")
    public List<AttendanceStatsData> getAttendanceStats(
            @Valid AttendanceStatsDto dto
    ) {
        dto.validate();

        // HINT: I have to ignore pagination for now, data is destructed via multiple tables, with aggregation methods needed & loading data.
        dto.setCustomerId(List.of(Utils.getLoggedInCustomerId()));
        return externalService.getAttendanceStats(dto);
    }
}
