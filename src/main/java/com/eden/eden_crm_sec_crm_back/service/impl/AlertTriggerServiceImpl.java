package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.PatrolFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.VisitorFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerDTO;
import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;
import com.eden.eden_crm_sec_crm_back.dto.TriggerWithAlertTriggerResponse;
import com.eden.eden_crm_sec_crm_back.entity.AlertTrigger;
import com.eden.eden_crm_sec_crm_back.entity.AlertTriggerSeverity;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.TriggerMapper;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerRepository;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerSeverityRepository;
import com.eden.eden_crm_sec_crm_back.repository.projections.AlertTriggerWithSeverityProjection;
import com.eden.eden_crm_sec_crm_back.service.AlertTriggerService;
import com.eden.eden_crm_sec_crm_back.service.TriggerService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertTriggerServiceImpl implements AlertTriggerService {

    private final AlertTriggerRepository alertTriggerRepository;
    private final AlertTriggerSeverityRepository alertTriggerSeverityRepository;
    private final Utils utils;
    private final AttendanceFeignClient attendanceFeignClient;
    private final PatrolFeignClient patrolFeignClient;
    private final VisitorFeignClient visitorFeignClient;
    private final TriggerService triggerService;
    private final TriggerMapper triggerMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<ServicePlatformEnum, List<TriggerWithAlertTriggerResponse>> getAllAlertTriggers() {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        List<AlertTriggerWithSeverityProjection> alertTriggers = alertTriggerRepository.findAllWithSeverityByCustomerId(customerId);
        Map<ServicePlatformEnum, List<TriggerWithAlertTriggerResponse>> triggerMap = new EnumMap<>(ServicePlatformEnum.class);
        if (alertTriggers.isEmpty())
            return triggerMap;

        Map<ServicePlatformEnum, Map<Long, TriggerResponse>> triggers = new EnumMap<>(ServicePlatformEnum.class);
        Set<ServicePlatformEnum> servicePlatforms = alertTriggers.stream().map(AlertTriggerWithSeverityProjection::getServicePlatformName).collect(Collectors.toSet());
        if (servicePlatforms.contains(ServicePlatformEnum.PATROLS))
            servicePlatforms.remove(ServicePlatformEnum.INCIDENTS);

        servicePlatforms.forEach(servicePlatform -> {
            List<TriggerResponse> responses = getTriggers(servicePlatform);
            responses.forEach(response -> {
                triggers.putIfAbsent(servicePlatform, new HashMap<>());
                triggers.get(servicePlatform).put(response.getId(), response);
            });
        });

        alertTriggers.forEach(alertTrigger -> {
            triggerMap.putIfAbsent(alertTrigger.getServicePlatformName(), new ArrayList<>());
            ServicePlatformEnum servicePlatformEnum = alertTrigger.getServicePlatformName();
            if (servicePlatformEnum == ServicePlatformEnum.INCIDENTS)
                servicePlatformEnum = ServicePlatformEnum.PATROLS;

            TriggerResponse triggerResponse = triggers.get(servicePlatformEnum).get(alertTrigger.getTriggerId());
            TriggerWithAlertTriggerResponse triggerWithAlertTriggerResponse = triggerMapper.toTriggerWithAlertTriggerResponse(triggerResponse);
            triggerWithAlertTriggerResponse.setId(alertTrigger.getId());
            triggerMap.get(servicePlatformEnum).add(triggerWithAlertTriggerResponse);
        });
        return triggerMap;
    }

    @Override
    @Transactional
    public AlertTriggerDTO setAlertTriggerSeverity(Long id, AlertTriggerSeverityRequest request) {
        Long customerId = utils.getLoggedInUser().getCustomerId();
//        Long customerId = 1L;
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

    private List<TriggerResponse> getTriggers(ServicePlatformEnum servicePlatformName) {
        return switch (servicePlatformName) {
            case ATTENDANCE -> attendanceFeignClient.getAllTriggers();
            case PATROLS, INCIDENTS -> patrolFeignClient.getAllTriggers();
            case VISITORS -> visitorFeignClient.getAllTriggers();
            case CRM -> triggerService.getAllTriggers();
            default -> throw new BusinessException("Invalid service platform name", HttpStatus.BAD_REQUEST);
        };
    }
}
