package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record TodayTaskEntryResponse(
    Long taskId,
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Populated for new-path distributions (task_management module).
    // CLEANUP: rename to taskId after Phase E and drop the old taskId field.
    Long taskDefinitionId,
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
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
    BigDecimal latitude,
    BigDecimal longitude,

    Long taskDistributionId,
    DistributionType distributionType,
    Long patrolDistributionId,
    Long immediateDistributionId,

    Integer displayOrder,

    List<TodayTaskExecutionSlotEntryResponse> executionSlots
) {}
