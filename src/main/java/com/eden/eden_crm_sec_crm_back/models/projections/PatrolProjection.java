package com.eden.eden_crm_sec_crm_back.models.projections;

public interface PatrolProjection {
    Long getPatrolId();
    Long getPatrolDetailId();
    String getPatrolName();
    String getFrequency();
    String getFrequencyRate();
    String getLocationName();
    String getTaskName();
}
