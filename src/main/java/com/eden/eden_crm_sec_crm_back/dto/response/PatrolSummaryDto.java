package com.eden.eden_crm_sec_crm_back.dto.response;

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
