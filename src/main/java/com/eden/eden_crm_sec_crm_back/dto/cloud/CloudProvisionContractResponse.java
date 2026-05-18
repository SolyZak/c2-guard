package com.eden.eden_crm_sec_crm_back.dto.cloud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudProvisionContractResponse {
    private Long externalContractId;
    private String status; // "PROVISIONED" or "FAILED"
}
