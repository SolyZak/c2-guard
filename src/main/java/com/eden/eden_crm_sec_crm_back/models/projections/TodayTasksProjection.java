package com.eden.eden_crm_sec_crm_back.models.projections;

import java.time.LocalDate;
import java.time.OffsetTime;

public interface TodayTasksProjection {
    String getTaskName();
    Long getPatrolId();
    String getPatrolName();
    Long getLocationId();
    String getLocationName();
    Long getPremiseId();
    String getPremiseName();
    LocalDate getEndDate();
    LocalDate getStartDate();
    OffsetTime getStartTime();
    OffsetTime getEndTime();
    String getPatrolFreqType();

    Long getPatrolDistributionId();
    Long getTaskId();
    String getPeriodStatus();
}
