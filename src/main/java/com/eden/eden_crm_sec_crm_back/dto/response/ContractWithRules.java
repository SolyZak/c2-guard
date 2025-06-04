package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.PresenceMode;
import lombok.Data;

@Data
public class ContractWithRules {
    private Long id;
    private String agreementNumber;
    private String agreementName;
    private Integer checkInBeforeMinutes;
    private Integer checkInAfterMinutes;
    private Integer checkOutBeforeMinutes;
    private PresenceMode presenceMode;
}
