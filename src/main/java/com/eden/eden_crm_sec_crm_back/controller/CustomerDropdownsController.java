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
@RequestMapping(path = "/customers")
@RequiredArgsConstructor
@Tag(
        name = "Customer Dropdown APIs",
        description = "This part is for customer portal, will provide the needed for any dropdown APIs")
public class CustomerDropdownsController {

    private final DropdownService dropdownService;

    @GetMapping("/security-companies/dropdown")
    public ApiResponse<List<GeneralDropdown>> myContractedSecurityCompaniesDropdown() {
        /**
         * todo this api must be called from here with org unit
         * for getting the security companies ids then query org unit to fetch the data related to those ids
         * but for now we just query in crm db which has security company id & name in contracts table
         */
        return ApiResponse.ok(dropdownService.customerContractedSecurityCompaniesDropdown(Utils.getLoggedInCustomerId()));
    }

    @GetMapping("/contracts/dropdown")
    public ApiResponse<List<GeneralDropdown>> myContractsDropdown(
            @RequestParam(name = "securityCompanyId", required = false) Long securityCompanyId
    ) {
        return ApiResponse.ok(dropdownService.myContractsDropdown(Utils.getLoggedInCustomerId(), securityCompanyId));
    }

    @GetMapping("/operation-sites/dropdown")
    public ApiResponse<List<GeneralDropdown>> myOperationSitesDropdown(
            @RequestParam(name = "securityCompanyId", required = false) Long securityCompanyId,
            @RequestParam(name = "contractId", required = false) Long contractId
    ) {
        return ApiResponse.ok(dropdownService.myOperationSitesDropdown(Utils.getLoggedInCustomerId(), securityCompanyId, contractId));
    }
}
