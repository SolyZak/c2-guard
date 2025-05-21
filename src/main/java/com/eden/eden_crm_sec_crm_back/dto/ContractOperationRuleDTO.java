package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.enums.PresenceMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class ContractOperationRuleDTO extends BaseDto<Long> implements Serializable {

    private Long customerAgreementId;
    private boolean allowCheckInBefore;
    private Integer checkInBeforeMinutes;
    private boolean allowCheckInAfter;
    private Integer checkInAfterMinutes;
    private boolean allowCheckOutAfter;
    private Integer checkOutAfterMinutes;
    @Enumerated(EnumType.STRING)
    private PresenceMode presenceMode;
}
