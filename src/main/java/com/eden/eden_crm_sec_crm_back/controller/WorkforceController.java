package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/workforce")
@Tag(
        name = "Workforce App APIs",
        description = "This part is for workforce app part of the system it'll provide the needed APIs for workforce landing screen"
)
public class WorkforceController {
    @Operation(summary = "Get customers dropdown list, that's workforce security company contracted with")
    @GetMapping("/customers/dropdown")
    public ApiResponse<List<GeneralDropdown>> customersDropdown() {
        // todo get customers that's relate to the logged in workforce security company,
        //  by contract and today is in the contract
        return ApiResponse.ok(List.of());
    }

    @Operation(summary = "Get contracts dropdown list related to customer, that's workforce security company contracted with")
    @GetMapping("/customers/{id}/contracts")
    public ApiResponse<List<GeneralDropdown>> customersContractDropdown(@PathVariable("id") Long customerId) {
        // todo get contracts that's relate to the logged in workforce security company,
        //  by contract and today is in the contract + customer selected
        return ApiResponse.ok(List.of());
    }

    @Operation(summary = "Get operations sites dropdown list related to customer & contract, that's workforce security company contracted with")
    @GetMapping("/customers/{id}/contracts/{contractId}/operation-sites")
    public ApiResponse<List<GeneralDropdown>> customersContractOperationSitesDropdown(
            @PathVariable("id") Long id,
            @PathVariable("contractId") Long contractId
    ) {
        // todo get operation sites that's relate to the logged in workforce security company,
        //  by contract and today is in the contract + customer & contract selected
        return ApiResponse.ok(List.of());
    }

    @Operation(summary = "Get operations sites dropdown list")
    @GetMapping("/operation-sites/dropdown")
    public ApiResponse<List<GeneralDropdown>> operationSitesDropdown() {
        // todo get all operation sites
        return ApiResponse.ok(List.of());
    }
}
