package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class SiteDistributionDto {
    @NotNull(message = "{validation.distribution.siteId.required}")
    @Positive(message = "{validation.distribution.siteId.positive}")
    private Long siteId;

    @NotEmpty(message = "{validation.distribution.activities.required}")
    @NotNull(message = "{validation.distribution.activities.required}")
    private Set<ActivityEnum> activities;

    @Valid
    @NotEmpty(message = "{validation.distribution.details.required}")
    @NotNull(message = "{validation.distribution.details.required}")
    private List<LKCustomerContractOperationServiceDto> operationServices;
}
