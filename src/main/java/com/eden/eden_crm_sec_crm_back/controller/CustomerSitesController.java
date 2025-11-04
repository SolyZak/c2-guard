package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteJobDescResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSitePremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/customer/operation-sites")
@RequiredArgsConstructor
@Tag(
        name = "Operation sites API",
        description = "This part is for customer portal, will provide the needed for operation sites API")
public class CustomerSitesController {
    // HINT: this controller must be used by customers to manage them operation sites
    private final CustomerSiteService customerSiteService;

    @PostMapping
    @Operation(summary = "Create operation site")
    ApiResponse<CustomerSiteResponseDto> addSite(@RequestBody @Valid CustomerSiteRequestDto requestDto) {
        return ApiResponse.ok(customerSiteService.addCustomerSite(requestDto));
    }

    @Operation(summary = "Update operation site")
    @PutMapping("/{id}")
    ApiResponse<String> updateSite(@PathVariable Long id, @RequestBody @Valid UpdateCustomerSiteRequestDto requestDto) {
        customerSiteService.updateCustomerSite(id, requestDto);
        return ApiResponse.ok(MessageUtil.getMessage("operation-site.updated"));
    }

    @Operation(summary = "Get All operation sites")
    @GetMapping
    ApiResponse<List<CustomerSiteResponseDto>> myCustomerSites() {
        return ApiResponse.ok(customerSiteService.getSitesForCustomer());
    }

    @Operation(summary = "Get operation site")
    @DeleteMapping("/{id}")
    ApiResponse<String> deleteCustomerSite(@PathVariable Long id) {
        return ApiResponse.ok(customerSiteService.deleteSiteForCustomer(id));
    }

    @GetMapping("/paginate")
    ApiResponse<PaginateResponse<CustomerSitePremiseResponseDto>> getAllPremisesPaginated(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "search") String search) {
        return ApiResponse.ok(customerSiteService.getSitesPaginated(search, page, size));
    }
}
