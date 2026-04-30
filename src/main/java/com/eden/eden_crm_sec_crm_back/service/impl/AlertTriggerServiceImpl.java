package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.PatrolFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.VisitorFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.dto.ServicePlatformWithTriggersResponse;
import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;
import com.eden.eden_crm_sec_crm_back.dto.TriggerWithAlertTriggerResponse;
import com.eden.eden_crm_sec_crm_back.entity.AlertTrigger;
import com.eden.eden_crm_sec_crm_back.entity.AlertTriggerSeverity;
import com.eden.eden_crm_sec_crm_back.entity.ServicePlatform;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.TriggerMapper;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerRepository;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerSeverityRepository;
import com.eden.eden_crm_sec_crm_back.repository.ServicePlatformRepository;
import com.eden.eden_crm_sec_crm_back.repository.projections.AlertTriggerWithSeverityProjection;
import com.eden.eden_crm_sec_crm_back.service.AlertTriggerService;
import com.eden.eden_crm_sec_crm_back.service.TriggerService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertTriggerServiceImpl implements AlertTriggerService {

    private static final Set<ServicePlatformEnum> EXCLUDED_PLATFORMS = Set.of(ServicePlatformEnum.CRM);

    private final AlertTriggerRepository alertTriggerRepository;
    private final AlertTriggerSeverityRepository alertTriggerSeverityRepository;
    private final ServicePlatformRepository servicePlatformRepository;
    private final Utils utils;
    private final AttendanceFeignClient attendanceFeignClient;
    private final PatrolFeignClient patrolFeignClient;
    private final VisitorFeignClient visitorFeignClient;
    private final TriggerService triggerService;
    private final TriggerMapper triggerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServicePlatformWithTriggersResponse> getAllAlertTriggers() {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        List<ServicePlatform> servicePlatforms = servicePlatformRepository.findAll();
        Map<ServicePlatformEnum, ServicePlatform> servicePlatformMap = servicePlatforms.stream()
                .collect(Collectors.toMap(ServicePlatform::getCode, Function.identity()));

        // Fetch alert triggers excluding Patrols (CRM) platform at DB level
        List<AlertTriggerWithSeverityProjection> alertTriggers =
                alertTriggerRepository.findAllWithSeverityByCustomerIdExcludingPlatforms(customerId, EXCLUDED_PLATFORMS);

        Map<ServicePlatformEnum, List<TriggerWithAlertTriggerResponse>> triggerMap = new EnumMap<>(ServicePlatformEnum.class);
        List<ServicePlatformWithTriggersResponse> servicePlatformWithTriggersResponses = new ArrayList<>();
        if (alertTriggers.isEmpty())
            return servicePlatformWithTriggersResponses;

        Map<ServicePlatformEnum, Map<Long, TriggerResponse>> triggers = new EnumMap<>(ServicePlatformEnum.class);
        Set<ServicePlatformEnum> servicePlatformCodes = alertTriggers.stream()
                .map(AlertTriggerWithSeverityProjection::getServicePlatformCode)
                .collect(Collectors.toSet());

        servicePlatformCodes.forEach(servicePlatform -> {
            List<TriggerResponse> responses = getTriggers(servicePlatform);
            responses.forEach(response -> {
                triggers.putIfAbsent(servicePlatform, new HashMap<>());
                triggers.get(servicePlatform).put(response.getId(), response);
            });
        });

        alertTriggers.forEach(alertTrigger -> {
            triggerMap.putIfAbsent(alertTrigger.getServicePlatformCode(), new ArrayList<>());
            ServicePlatformEnum servicePlatformEnum = alertTrigger.getServicePlatformCode();

            TriggerResponse triggerResponse = triggers.get(servicePlatformEnum).get(alertTrigger.getTriggerId());
            TriggerWithAlertTriggerResponse triggerWithAlertTriggerResponse = triggerMapper.toTriggerWithAlertTriggerResponse(triggerResponse);
            triggerWithAlertTriggerResponse.setId(alertTrigger.getId());
            triggerWithAlertTriggerResponse.setSeverity(alertTrigger.getSeverity());
            triggerMap.get(servicePlatformEnum).add(triggerWithAlertTriggerResponse);
        });

        triggerMap.forEach((servicePlatformEnum, triggerList) -> {
            ServicePlatform servicePlatform = servicePlatformMap.get(servicePlatformEnum);
            servicePlatformWithTriggersResponses.add(
                    triggerMapper.toServicePlatformWithTriggersResponse(servicePlatform, triggerList)
            );
        });
        return servicePlatformWithTriggersResponses;
    }

    @Override
    @Transactional
    public void setAlertTriggerSeverity(Long id, AlertTriggerSeverityRequest request) {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        AlertTrigger alertTrigger = alertTriggerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("AlertTrigger not found", HttpStatus.NOT_FOUND));
        AlertTriggerSeverity alertTriggerSeverity = alertTriggerSeverityRepository
                .findByAlertTrigger_IdAndCustomerId(alertTrigger.getId(), customerId)
                .orElseGet(AlertTriggerSeverity::new);

        alertTriggerSeverity.setAlertTrigger(alertTrigger);
        alertTriggerSeverity.setCustomerId(customerId);
        alertTriggerSeverity.setSeverity(request.severity());

        alertTriggerSeverityRepository.saveAndFlush(alertTriggerSeverity);
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