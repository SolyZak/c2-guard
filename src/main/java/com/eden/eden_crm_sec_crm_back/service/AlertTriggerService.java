package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.dto.ServicePlatformWithTriggersResponse;

import java.util.List;

public interface AlertTriggerService {
    List<ServicePlatformWithTriggersResponse> getAllAlertTriggers();
    void setAlertTriggerSeverity(Long id, AlertTriggerSeverityRequest request);
}
