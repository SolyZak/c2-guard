package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.external.*;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.external.AttendanceWorkingPeriodData;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.service.ExternalService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/external")
@RequiredArgsConstructor
@Tag(
        name = "External, Service To Service APIs",
        description = "This part is for service to service usage, will provide the needed from crm service to other services")
public class ExternalController {

    private final ExternalService externalService;
    private final Utils utils;

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
        dto.setCustomerId(List.of(getLoggedInCustomerId()));
        return externalService.getAttendanceStats(dto);
    }

    @Operation(summary = "Get customer attendance stats for operation site")
    @GetMapping("/customer/attendance/working-periods")
    public List<AttendanceWorkingPeriodData> getAttendanceDateWorkingPeriod(
            @RequestParam(name = "contractId") Long contractId,
            @RequestParam(name = "operationSiteId") Long operationSiteId,
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return externalService.getAttendanceDateWorkingPeriod(getLoggedInCustomerId(), contractId, operationSiteId, date);
    }

    @Operation(summary = "Get customer contracts planned quantities")
    @GetMapping("/customer/contracts/planned-quantities")
    public List<ContractPlannedQntDto> getContractPlannedQnt(
            @RequestParam(name = "securityCompanyId", required = false) Long securityCompanyId,
            @RequestParam(name = "contractId", required = false) List<Long> contractId,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return externalService.getContractPlannedQnt(getLoggedInCustomerId(), securityCompanyId, contractId, from, to);
    }

    @Operation(summary = "Get logged in customer user data")
    @GetMapping("/customer/user/info")
    public UserData getLoggedInCustomerUser() {
        return utils.getLoggedInUser();
    }

    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }
}
