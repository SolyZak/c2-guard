package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ServicePlatformWithTriggersResponse {
    private Long id;
    private String name;
    private String nameAr;
    private ServicePlatformEnum code;
    private List<TriggerWithAlertTriggerResponse> triggers;
}
