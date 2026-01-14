package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerDTO;
import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.dto.TriggerWithAlertTriggerResponse;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;

import java.util.List;
import java.util.Map;

public interface AlertTriggerService {
    Map<ServicePlatformEnum, List<TriggerWithAlertTriggerResponse>> getAllAlertTriggers();
    AlertTriggerDTO setAlertTriggerSeverity(Long id, AlertTriggerSeverityRequest request);
}
