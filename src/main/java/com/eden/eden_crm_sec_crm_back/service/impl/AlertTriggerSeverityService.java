package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.entity.AlertTriggerSeverity;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerSeverityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertTriggerSeverityService {
    private final AlertTriggerSeverityRepository alertTriggerSeverityRepository;

    @Transactional
    public List<AlertTriggerSeverity> findByTriggerIdAndServicePlatformId(final Long triggerId,
                                                                          final Long servicePlatformId) {
        return alertTriggerSeverityRepository.findByAlertTrigger_TriggerIdAndAlertTrigger_ServicePlatform_Id(triggerId, servicePlatformId);
    }

    @Transactional
    public List<AlertTriggerSeverity> findByTriggerIdAndServicePlatformIdAndCustomerId(final Long triggerId,
                                                                                       final Long servicePlatformId,
                                                                                       final Long customerId) {
        return alertTriggerSeverityRepository.findByAlertTrigger_TriggerIdAndAlertTrigger_ServicePlatform_IdAndCustomerId(
                triggerId, servicePlatformId, customerId);
    }
}
