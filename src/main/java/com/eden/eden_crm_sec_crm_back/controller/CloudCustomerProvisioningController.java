package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionC2GuardCustomerRequest;
import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionCustomerResponse;
import com.eden.eden_crm_sec_crm_back.service.CloudCustomerProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/cloud/customers")
@RequiredArgsConstructor
public class CloudCustomerProvisioningController {

    private final CloudCustomerProvisioningService provisioningService;

    @PostMapping("/provision")
    public CloudProvisionCustomerResponse provision(@RequestBody CloudProvisionC2GuardCustomerRequest request) {
        return provisioningService.provision(request);
    }
}
