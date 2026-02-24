package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections;

import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface TodayTaskSlotProjection {
    Long getId();
    Long getTaskId();
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Points to task_definition (task_management module) for new-path distributions.
    // CLEANUP: rename this to getTaskId() after Phase E and remove the old getTaskId().
    Long getTaskDefinitionId();
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
    Long getPatrolId();
    Long getPremiseId();
    Long getLocationId();

    String getTaskName();
    String getPatrolName();
    String getLocationName();
    String getPremiseName();

    String getFrequency();
    String getFrequencyRate();

    OffsetDateTime getStartDateTime();
    OffsetDateTime getEndDateTime();
    String getAccessType();

    BigDecimal getLongitude();
    BigDecimal getLatitude();
    TaskDistributionStatus getStatus();

    Long getTaskDistributionId();
    DistributionType getDistributionType();
    Long getPatrolDistributionId();
    Long getImmediateDistributionId();
}
