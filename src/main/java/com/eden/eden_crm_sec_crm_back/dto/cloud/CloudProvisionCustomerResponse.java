package com.eden.eden_crm_sec_crm_back.dto.cloud;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloudProvisionCustomerResponse {
    private Long externalCustomerId;
    private String status;
    private String message;
}
