package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.service.DropdownService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        return ApiResponse.ok(dropdownService.customerContractedSecurityCompaniesDropdown(Utils.getLoggedInCustomerId()));
    }
}
