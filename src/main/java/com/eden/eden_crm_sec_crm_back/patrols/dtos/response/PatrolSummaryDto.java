package com.eden.eden_crm_sec_crm_back.patrols.dtos.response;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record PatrolSummaryDto(
    Long id,
    String name,
    OffsetDateTime startDateTime,
    String frequencyType,
    Long assignedTasksCount,
    Long finishedTasksCount
) {}
