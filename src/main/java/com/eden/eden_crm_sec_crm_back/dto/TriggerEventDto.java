package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.enums.Severity;
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
    // Severity from task/check definition. When set, C2AlertEventService uses it directly
    // instead of looking up AlertTriggerSeverity table.
    private Severity overrideSeverity;
    // Location entity ID — set only for patrol task (missed/deviation) events.
    private Long locationId;
}
