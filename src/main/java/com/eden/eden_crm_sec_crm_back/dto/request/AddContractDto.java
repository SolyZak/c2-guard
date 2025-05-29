package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddContractDto {
    @NotNull(message = "{validation.contract.agreementNumber.required}")
    private String agreementNumber;

    @NotNull(message = "{validation.contract.agreementName.required}")
    private String agreementName;

    @NotNull(message = "{validation.contract.startAgreementDate.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startAgreementDate;

    @NotNull(message = "{validation.contract.endAgreementDate.required}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endAgreementDate;

    @NotNull(message = "{validation.contract.securityCompanyId.required}")
    private Long securityCompanyId;

    private Long currency;

    @Valid
    @NotEmpty(message = "{validation.contract.services.required}")
    private List<AddContractServiceDto> services;
}
