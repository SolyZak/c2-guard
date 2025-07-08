package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.service.DropdownService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/security-company")
@RequiredArgsConstructor
@Tag(
        name = "Security Company Dropdown APIs",
        description = "This part is for security company portal, will provide the needed for any dropdown APIs")
public class SecurityCompanyDropdownsController {

    private final DropdownService dropdownService;

    @GetMapping("/customers/dropdown")
    public ApiResponse<List<GeneralDropdown>> customersDropdown() {
        return ApiResponse.ok(dropdownService.securityCompanyCustomersDropdown(Utils.getLoggedInSecurityCompanyId()));
    }

    @GetMapping("/contracts/dropdown")
    public ApiResponse<List<GeneralDropdown>> myContractsDropdown(
            @RequestParam(name = "customerId", required = false) List<Long> customerId
    ) {
        return ApiResponse.ok(dropdownService.securityCompanyContractsDropdown(Utils.getLoggedInSecurityCompanyId(), customerId));
    }

    @GetMapping("/operation-sites/dropdown")
    public ApiResponse<List<GeneralDropdown>> myOperationSitesDropdown(
            @RequestParam(name = "customerId", required = false) List<Long> customerId,
            @RequestParam(name = "contractId", required = false) List<Long> contractId
    ) {
        return ApiResponse.ok(
                dropdownService.securityCompanyOperationSitesDropdown(
                        Utils.getLoggedInSecurityCompanyId(),
                        customerId,
                        contractId
                )
        );
    }
}
