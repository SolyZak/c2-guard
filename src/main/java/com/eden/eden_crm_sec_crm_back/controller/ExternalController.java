package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.external.CustomerInfo;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteData;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteInfo;
import com.eden.eden_crm_sec_crm_back.service.ExternalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
