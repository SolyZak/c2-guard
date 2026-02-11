package com.eden.eden_crm_sec_crm_back.dto.external;

import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInData {
    private Long workforceId;
    private String workforceName;
    private Long operationSiteId;
    private String operationSiteName;
    private CustomTimezone timezone;
    private Long contractId;
    private String contractName;
    private Long customerId;
    private String customerName;
    private Long securityCompanyId;
    private String securityCompanyName;
}
