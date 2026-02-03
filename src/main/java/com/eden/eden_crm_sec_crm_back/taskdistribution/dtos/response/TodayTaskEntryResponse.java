package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record TodayTaskEntryResponse(
    Long taskId,
    Long patrolId,
    Long premiseId,
    Long locationId,

    String taskName,
    String patrolName,
    String locationName,
    String premiseName,

    String patrolFrequency,
    String patrolFrequencyRate,

    OffsetDateTime endDateTime,
    String accessType,

    Long taskDistributionId,
    DistributionType distributionType,
    Long patrolDistributionId,
    Long immediateDistributionId,

    List<TodayTaskExecutionSlotEntryResponse> executionSlots
) {}
