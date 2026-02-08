package com.eden.eden_crm_sec_crm_back.patrols.dtos.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PatrolSummaryDto(
    Long id,
    String name,
    LocalDate startDate,
    String frequencyType,
    Long assignedTasksCount,
    Long finishedTasksCount
) {}
