package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertTriggerDTO {
    private Long id;
    private Long alertId;
    private Long triggerId;
    private Long servicePlatformId;
    private Severity severity;
}
