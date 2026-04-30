package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import java.time.Instant;

public interface TaskCheckComparisonReportProjection {
    Long getComparisonId();
    Instant getComparisonDate();
    Double getComparisonRatio();
    Boolean getMatching();
    Long getTaskDefinitionId();
    String getTaskDefinitionName();
    Long getCheckDefinitionId();
    String getCheckDefinitionName();
    String getCheckBaseImagePath();
    Long getCheckExecutionId();
    String getCheckTransactionImagePath();
    Long getWorkforceId();
    Long getLocationId();
    String getLocationName();
    String getMissingQualityRaw();
}