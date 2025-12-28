package com.eden.eden_crm_sec_crm_back.models.projections;

import java.time.LocalDate;

public interface PatrolPremiseAggregation {
    Long getPremiseId();
    Long getPatrolId();
    String getPatrolName();
    LocalDate getPatrolStartDate();
    String getPatrolFrequencyType();
    Long getAssignedCount();
    Long getFinishedCount();
}
