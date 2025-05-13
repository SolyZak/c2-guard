package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/operation-sites")
@RequiredArgsConstructor
public class CustomerSitesController {
    // HINT: this controller must be used by customers to manage them operation sites
    private final CustomerSiteService customerSiteService;

    @PostMapping
    ApiResponse<CustomerSiteResponseDto> addSite(@RequestBody @Valid CustomerSiteRequestDto requestDto) {
        return ApiResponse.ok(customerSiteService.addCustomerSite(requestDto));
    }

    @PutMapping("/{id}")
    ApiResponse<String> updateSite(@PathVariable Long id, @RequestBody @Valid UpdateCustomerSiteRequestDto requestDto) {
        customerSiteService.updateCustomerSite(id, requestDto);
        return ApiResponse.ok(MessageUtil.getMessage("operation-site.updated"));
    }

    @GetMapping
    ApiResponse<List<CustomerSiteResponseDto>> myCustomerSites() {
        return ApiResponse.ok(customerSiteService.getSitesForCustomer());
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteCustomerSite(@PathVariable Long id) {
        return ApiResponse.ok(customerSiteService.deleteSiteForCustomer(id));
    }
}
