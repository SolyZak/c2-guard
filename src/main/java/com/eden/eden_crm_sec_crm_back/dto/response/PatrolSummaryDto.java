package com.eden.eden_crm_sec_crm_back.dto.response;

public record PatrolSummaryDto(
        Long patrolId,
        String patrolName,
        String patrolStartDate,
        String patrolFrequencyType,
        int patrolAssignedTasksCount,
        int patrolFinishedTasksCount
) {}
