package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationSiteInfo {
    private Long id;
    private String name;
    private Long operationSiteId;
    private String operationSiteName;
    private Long contractId;
    private String contractName;
    private Long customerId;
    private String customerName;
    private Long securityCompanyId;
    private String securityCompanyName;
}
