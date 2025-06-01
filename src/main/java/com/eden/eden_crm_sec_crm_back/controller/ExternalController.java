package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.external.CustomerInfo;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteData;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteInfo;
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
    @Operation(summary = "Get operation site info for today", description = "This API will provide information abut operation site today status")
    @GetMapping("/operation-sites/{id}")
    public OperationSiteInfo getOperationSiteDetails(@PathVariable(name = "id") Long id) {
        // todo to be implemented
        return OperationSiteInfo.builder()
                .id(1)
                .name("Test Operation Site")
                .operationSiteName("Test Operation Site")
                .operationSiteId(1L)
                .customerId(1)
                .customerName("Test Customer")
                .build();
    }

    @Operation(summary = "Get all operation sites for a customer")
    @GetMapping("/operation-sites/{id}/all")
    public List<OperationSiteData> getCustomerOperationSites(@PathVariable(name = "id") Long customerId) {
        // todo to be implemented
        return List.of(OperationSiteData.builder()
                .id(1L)
                .name("Test Operation Site")
                .tolerance(500.0)
                .latitude(31.33333333333)
                .longitude(30.3333333333)
                .build());
    }

    @Operation(summary = "Get customer info", description = "This API will provide information abut customer")
    @GetMapping("/customers/{id}")
    public CustomerInfo getCustomerInfo(@PathVariable(name = "id") Long id) {
        // todo to be implemented
        return CustomerInfo.builder()
                .id(1L)
                .name("Test Customer")
                .code("123123")
                .build();
    }
}
