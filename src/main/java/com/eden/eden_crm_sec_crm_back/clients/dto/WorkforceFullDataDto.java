package com.eden.eden_crm_sec_crm_back.clients.dto;

public record WorkforceFullDataDto(
        WorkforceDataDto workforce,
        SecurityCompanyDataDto securityCompany
) {
}
