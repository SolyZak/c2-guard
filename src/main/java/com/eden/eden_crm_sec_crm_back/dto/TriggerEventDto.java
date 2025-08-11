package com.eden.eden_crm_sec_crm_back.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggerEventDto {
    @NonNull
    private Long serviceTriggerEventId;
    @NonNull
    private Long triggerId;
    @NonNull
    private String triggerName;
    @NonNull
    private Long workforceId;
    @NonNull
    private Long operationSiteId;
    @NonNull
    private Long customerId;
    @NonNull
    private Double longitude;
    @NonNull
    private Double latitude;
    @NonNull
    private OffsetTime eventTime;
    @NonNull
    private LocalDate eventDate;
    @NonNull
    private String description;
    @NonNull
    private String servicePlatformName;
}
