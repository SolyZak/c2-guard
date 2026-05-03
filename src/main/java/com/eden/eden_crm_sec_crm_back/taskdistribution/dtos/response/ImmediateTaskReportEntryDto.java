package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ImmediateTaskReportEntryDto(
    Long id,
    OffsetDateTime startDateTime,
    OffsetDateTime endDateTime,
    String workforceName,
    String status,
    String locationName,
    String taskName
) {}
