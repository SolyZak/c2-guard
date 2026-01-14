package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerDTO;
import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.entity.AlertTrigger;
import com.eden.eden_crm_sec_crm_back.entity.AlertTriggerSeverity;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerRepository;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerSeverityRepository;
import com.eden.eden_crm_sec_crm_back.repository.projections.AlertTriggerWithSeverityProjection;
import com.eden.eden_crm_sec_crm_back.service.AlertTriggerService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertTriggerServiceImpl implements AlertTriggerService {

    private final AlertTriggerRepository alertTriggerRepository;
    private final AlertTriggerSeverityRepository alertTriggerSeverityRepository;
    private final Utils utils;

    @Override
    @Transactional(readOnly = true)
    public List<AlertTriggerWithSeverityProjection> getAllAlertTriggers() {
//        Long customerId = utils.getLoggedInUser().getCustomerId();
        Long customerId = 1L;
        return alertTriggerRepository.findAllWithSeverityByCustomerId(customerId);
    }

    @Override
    @Transactional
    public AlertTriggerDTO setAlertTriggerSeverity(Long id, AlertTriggerSeverityRequest request) {
//        Long customerId = utils.getLoggedInUser().getCustomerId();
        Long customerId = 1L;
        AlertTrigger alertTrigger = alertTriggerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("AlertTrigger not found", HttpStatus.NOT_FOUND));
        AlertTriggerSeverity alertTriggerSeverity = alertTriggerSeverityRepository
            .findByAlertTrigger_IdAndCustomerId(alertTrigger.getId(), customerId)
            .orElseGet(AlertTriggerSeverity::new);

        alertTriggerSeverity.setAlertTrigger(alertTrigger);
        alertTriggerSeverity.setCustomerId(customerId);
        alertTriggerSeverity.setSeverity(request.severity());

        alertTriggerSeverity = alertTriggerSeverityRepository.saveAndFlush(alertTriggerSeverity);
        
        return AlertTriggerDTO.builder()
                .id(alertTrigger.getId())
                .alertId(alertTrigger.getAlertId())
                .triggerId(alertTrigger.getTriggerId())
                .servicePlatformId(alertTrigger.getServicePlatform().getId())
                .severity(alertTriggerSeverity.getSeverity())
                .build();
    }
}
