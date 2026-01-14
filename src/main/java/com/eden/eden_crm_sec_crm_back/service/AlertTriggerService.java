package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerDTO;
import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.repository.projections.AlertTriggerWithSeverityProjection;

import java.util.List;

public interface AlertTriggerService {
    List<AlertTriggerWithSeverityProjection> getAllAlertTriggers();
    AlertTriggerDTO setAlertTriggerSeverity(Long id, AlertTriggerSeverityRequest request);
}
