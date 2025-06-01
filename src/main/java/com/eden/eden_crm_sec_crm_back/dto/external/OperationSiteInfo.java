package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationSiteInfo {
    private Integer id;
    private String name;
    private Long operationSiteId;
    private String operationSiteName;
    private Integer contractId;
    private String contractName;
    private Integer customerId;
    private String customerName;
    private Integer securityCompanyId;
    private String securityCompanyName;
}
