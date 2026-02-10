package com.eden.eden_crm_sec_crm_back.patrols.repositories.projections;

import java.time.OffsetDateTime;

public interface PatrolPremiseAggregation {
    Long getPremiseId();
    String getPremiseName();
    String getPremiseCode();
    Long getPatrolId();
    String getPatrolName();
    OffsetDateTime getPatrolStartDateTime();
    String getPatrolFrequencyType();
    Long getAssignedCount();
    Long getFinishedCount();
}
