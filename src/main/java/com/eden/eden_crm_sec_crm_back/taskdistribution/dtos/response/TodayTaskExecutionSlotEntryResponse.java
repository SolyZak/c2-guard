package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record TodayTaskExecutionSlotEntryResponse(
    Long executionSlotId,
    OffsetDateTime startDateTime,
    OffsetDateTime endDateTime,
    TaskDistributionStatus status
) {}
