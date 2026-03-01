package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections;

public interface DistributableTaskProjection {
    Long getPatrolDetailId();
    String getTaskName();
    Long getTaskDefinitionId();
}
