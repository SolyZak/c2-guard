package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections;

import java.time.OffsetDateTime;

public interface ImmediateTaskReportProjection {
    Long getId();
    OffsetDateTime getStartDateTime();
    OffsetDateTime getEndDateTime();
    Long getWorkforceId();
    String getStatus();
    String getLocationName();
    String getTaskName();
}
