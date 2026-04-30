package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections;

import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface TodayTaskSlotProjection {
    Long getId();
    Long getTaskId();
    Long getTaskDefinitionId();
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
