package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ContractDistributionForPatrol;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.models.projections.DistributionTimesProjection;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.ContractDistributeService;
import com.eden.eden_crm_sec_crm_back.service.ContractOperationSiteDistributionPatrolService;
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
    private final ContractDistributeService contractDistributeService;
    private final ContractOperationSiteDistributionPatrolService contractOperationSiteDistributionPatrolService;

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

    @Operation(summary = "Get available operation sites for a selected contract API")
    @GetMapping("/{id}/operation-sites")
    public ApiResponse<List<GeneralDropdown>> availableOperationSitesList(
            @PathVariable("id") Long contractId
    ) {
        return ApiResponse.ok(customerContractService.availableOperationSitesList(contractId));
    }

    @Operation(summary = "Distribute A Contract Service & Operation Site API")
    @PutMapping("/{id}/distribute/{serviceId}")
    public ApiResponse<ContractDistributionResponseDto> contractDistribute(
            @PathVariable("id") Long contractId,
            @PathVariable("serviceId") Long serviceId,
            @RequestBody @Valid List<SiteDistributionDto> listDto
        ) {
        return ApiResponse.ok(contractDistributeService.contractDistribute(contractId, serviceId, listDto));
    }

    @Operation(summary = "Distribute Patrols")
    @PostMapping("/patrol/{id}/distribute/{serviceId}")
    public ApiResponse<String> contractDistributeForPatrol(
            @PathVariable("id") Long contractId,
            @PathVariable("serviceId") Long serviceId,
            @RequestBody @Valid List<ContractDistributionForPatrol> requestList
    ) {
        contractOperationSiteDistributionPatrolService.add(requestList, contractId, serviceId);
        return ApiResponse.created(null);
    }

    @Operation(summary = "Get times for a distribution")
    @GetMapping("/distribute/{id}")
    public ApiResponse<List<DistributionTimesProjection>> getTimesForDistribution(
            @PathVariable("id") Long id
    ) {
        return ApiResponse.ok(contractDistributeService.getAllStartEndTimesForDistribution(id));
    }

    @Operation(summary = "Get distributed operation sites for a selected contract & contract service API")
    @GetMapping("/distributed/operation-sites")
    public ApiResponse<List<DistributedOperationSite>> distributedOperationSites(
            @RequestParam(name = "contractId") Long contractId,
            @RequestParam(name = "lkCustomerContractServiceId") Long lkCustomerContractServiceId
    ) {
        return ApiResponse.ok(customerContractService.distributedOperationSites(contractId, lkCustomerContractServiceId));
    }

    @Operation(summary = "Get Contract Details API")
    @GetMapping("/{id}")
    public ApiResponse<ContractDetailsData> getContractDetails(
            @PathVariable("id") Long contractId
    ) {
        return ApiResponse.ok(customerContractService.getCustomerContractDetails(contractId));
    }

}
