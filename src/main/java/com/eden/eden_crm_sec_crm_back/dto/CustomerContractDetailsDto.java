package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractServiceDto;
import com.eden.eden_crm_sec_crm_back.enums.CurrencyEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class CustomerContractDetailsDto extends BaseDto<Long> implements Serializable {

    private String agreementNumber;
    private String agreementName;
    private LocalDate startAgreementDate;
    private LocalDate endAgreementDate;
    private Long securityCompanyId;
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;
    private ContractOperationRuleDTO contractOperationRuleDTO;
    private List<CustomerServiceDTO> serviceDTOList = new ArrayList<>();
    private List<SiteDistributionCustomDto> siteDistributionCustomDto = new ArrayList<>();
}
