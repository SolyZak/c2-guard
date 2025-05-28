package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.enums.PresenceMode;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class ContractOperationRuleDTO {
    @NotNull(message = "{validation.contract-operation-rule.contractId.required")
    private Long contractId;

    private boolean allowCheckInBefore;

    private Integer checkInBeforeMinutes;

    private boolean allowCheckInAfter;

    private Integer checkInAfterMinutes;

    private boolean allowCheckOutAfter;

    private Integer checkOutAfterMinutes;

    @NotNull(message = "{validation.contract-operation-rule.presenceMode.required")
    @Enumerated(EnumType.STRING)
    private PresenceMode presenceMode;

    public void validate() {
        if (allowCheckInBefore && checkInBeforeMinutes == null) {
            throw new BusinessException(
                    MessageUtil.getMessage("field.required", new Object[]{MessageUtil.getMessage("checkInBeforeMinutes")}),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
        if (allowCheckOutAfter && checkOutAfterMinutes == null) {
            throw new BusinessException(
                    MessageUtil.getMessage("field.required", new Object[]{MessageUtil.getMessage("checkOutAfterMinutes")}),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
        if (allowCheckInAfter && checkInAfterMinutes == null) {
            throw new BusinessException(
                    MessageUtil.getMessage("field.required", new Object[]{MessageUtil.getMessage("checkInAfterMinutes")}),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
    }
}
