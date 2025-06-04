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

    private Integer checkInBeforeMinutes;

    private Integer checkInAfterMinutes;

    private Integer checkOutBeforeMinutes;

    @NotNull(message = "{validation.contract-operation-rule.presenceMode.required")
    @Enumerated(EnumType.STRING)
    private PresenceMode presenceMode;

    public void validate() {
        if (checkInBeforeMinutes == null) {
            throw new BusinessException(
                    MessageUtil.getMessage("field.required", new Object[]{MessageUtil.getMessage("checkInBeforeMinutes")}),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
        if (checkInAfterMinutes == null) {
            throw new BusinessException(
                    MessageUtil.getMessage("field.required", new Object[]{MessageUtil.getMessage("checkInAfterMinutes")}),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
        if (presenceMode.equals(PresenceMode.BOTH) && checkOutBeforeMinutes == null) {
            throw new BusinessException(
                    MessageUtil.getMessage("field.required", new Object[]{MessageUtil.getMessage("checkOutBeforeMinutes")}),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
    }
}
