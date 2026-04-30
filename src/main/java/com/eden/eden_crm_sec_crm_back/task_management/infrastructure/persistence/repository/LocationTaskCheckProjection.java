package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

public interface LocationTaskCheckProjection {
    Long getLocationId();
    String getLocationName();
    Long getTaskDefinitionId();
    String getTaskDefinitionName();
    Long getCheckId();
    String getCheckName();
    String getImageUrl();

}