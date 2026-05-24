package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionContractRequest;
import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionContractResponse;
import com.eden.eden_crm_sec_crm_back.service.CloudContractProvisioningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/cloud/contracts")
@RequiredArgsConstructor
public class CloudContractProvisioningController {

    private final CloudContractProvisioningService provisioningService;

    @PostMapping("/provision")
    public CloudProvisionContractResponse provision(@RequestBody @Valid CloudProvisionContractRequest request) {
        return provisioningService.provision(request);
    }
}
