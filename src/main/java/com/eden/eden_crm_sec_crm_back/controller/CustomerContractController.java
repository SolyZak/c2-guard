package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractRowDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractServiceDetailsData;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/customers/contracts")
@RequiredArgsConstructor
@Tag(
        name = "Customer Contract API",
        description = "This part is for customer portal, will provide the needed for contracts API")
public class CustomerContractController {
    private final CustomerContractService customerContractService;

    @Operation(summary = "Create Contract API")
    @PostMapping
    public ApiResponse<String> createAgreement(@RequestBody @Valid AddContractDto dto) {
        return ApiResponse.ok(customerContractService.createAgreement(dto));
    }

    @Operation(summary = "Paginate Contracts API")
    @GetMapping
    public ApiResponse<PaginateResponse<ContractRowDto>> paginateMyContracts(
            @RequestParam(required = false, name = "search") String search,
            @RequestParam(required = false, name = "page", defaultValue = "0") int page,
            @RequestParam(required = false, name = "size", defaultValue = "10") int size,
            @RequestParam(required = false, name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false, name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.ok(customerContractService.paginateMyContracts(search, from, to, page, size));
    }

    @Operation(summary = "Get All My Drafted Contracts API")
    @GetMapping("/drafts/dropdown")
    public ApiResponse<List<ContractRowDto>> listMyDraftedContracts() {
        return ApiResponse.ok(customerContractService.listMyDraftedContracts());
    }

    @Operation(summary = "Get All My Contracts API")
    @GetMapping("/all/dropdown")
    public ApiResponse<List<ContractRowDto>> listAllMyContracts() {
        return ApiResponse.ok(customerContractService.listAllMyContracts());
    }

    @Operation(summary = "Get Contract Services List API")
    @GetMapping("/{id}/services")
    public ApiResponse<List<ContractServiceDetailsData>> contractServicesList(
            @PathVariable("id") Long contractId
    ) {
        return ApiResponse.ok(customerContractService.contractServicesList(contractId));
    }

    @Operation(summary = "Distribute A Contract Service & Operation Site API")
    @PutMapping("/{id}/distribute")
    public ApiResponse<String> contractDistribute(
            @PathVariable("id") Long contractId,
            @RequestBody @Valid SiteDistributionDto dto
        ) {
        return ApiResponse.ok(customerContractService.contractDistribute(contractId, dto));
    }

}
