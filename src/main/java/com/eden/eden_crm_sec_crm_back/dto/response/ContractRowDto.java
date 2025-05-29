package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractRowDto {
    private Long id;
    private String agreementNumber;
    private String agreementName;
    private String securityCompanyName;
    private String currencyCode;
    private LocalDate startAgreementDate;
    private LocalDate endAgreementDate;
    private ContractStatus status;
}
