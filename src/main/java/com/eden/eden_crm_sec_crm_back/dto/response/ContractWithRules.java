package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.PresenceMode;
import lombok.Data;

@Data
public class ContractWithRules {
    private Long id;
    private String agreementNumber;
    private String agreementName;
    private boolean allowCheckInBefore;
    private Integer checkInBeforeMinutes;
    private boolean allowCheckInAfter;
    private Integer checkInAfterMinutes;
    private boolean allowCheckOutAfter;
    private Integer checkOutAfterMinutes;
    private PresenceMode presenceMode;
}
