package com.eden.eden_crm_sec_crm_back.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TriggerWithAlertTriggerResponse {
    private Long id;
    private Long triggerId;
    private String name;
    private String nameAr;
    private String code;
    private String creationType;
    private String severity;
}
