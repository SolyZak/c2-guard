package com.eden.eden_crm_sec_crm_back.models.projections;

import java.time.LocalDate;
import java.time.OffsetTime;

public interface TodayTasksProjection {
    String getTaskName();
    String getPatrolName();
    String getLocationName();
    String getPremiseName();
    LocalDate getEndDate();
    OffsetTime getStartTime();
    OffsetTime getEndTime();
    String getPatrolFreqType();

    Long getPatrolDistributionId();
    Long getTaskId();
    String getPeriodStatus();
}
