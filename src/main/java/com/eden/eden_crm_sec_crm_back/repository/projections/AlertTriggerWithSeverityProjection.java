package com.eden.eden_crm_sec_crm_back.repository.projections;

import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.enums.Severity;

public interface AlertTriggerWithSeverityProjection {
    Long getId();
    Long getAlertId();
    Long getTriggerId();
    Long getServicePlatformId();
    ServicePlatformEnum getServicePlatformName();
    Severity getSeverity();
}
