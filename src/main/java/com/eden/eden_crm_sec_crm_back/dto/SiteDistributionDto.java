package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class SiteDistributionDto extends BaseDto<Long> implements Serializable {
    private Long siteId;  // Added for CustomerSite mapping
    private Set<ActivityEnum> activities;  // Added for activities
    private Long lkCustomerContractServiceId;
    private List<LKCustomerContractOperationServiceDto> operationServices;


}
