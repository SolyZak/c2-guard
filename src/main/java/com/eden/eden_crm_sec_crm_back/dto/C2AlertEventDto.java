package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.enums.Severity;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.OffsetTime;

@Data
@Builder
public class C2AlertEventDto {
    @NonNull
    private Long crmTriggerLogId;
    @NonNull
    private Long alertId;
    @NonNull
    private Long triggerId;
    @NonNull
    private String triggerName;
    @NonNull
    private Long servicePlatformId;
    @NonNull
    private String servicePlatformName;
    @NonNull
    private OffsetTime eventTime;
    @NonNull
    private LocalDate eventDate;
    @NonNull
    private Double longitude;
    @NonNull
    private Double latitude;
    @NonNull
    private Long operationSiteId;
    @NonNull
    private Long customerId;
    @NonNull
    private Long workforceId;
    @NonNull
    private Severity severity;
    @NonNull
    private String description; // in case of attendance this will be having workforce name
}
