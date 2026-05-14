package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.C2AlertEventDto;
import com.eden.eden_crm_sec_crm_back.entity.AlertTrigger;
import com.eden.eden_crm_sec_crm_back.entity.AlertTriggerSeverity;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.enums.Severity;
import com.eden.eden_crm_sec_crm_back.producer.C2EventProducer;
import com.eden.eden_crm_sec_crm_back.repository.AlertTriggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class C2AlertEventService {
    private final AlertTriggerSeverityService alertTriggerSeverityService;
    private final C2EventProducer c2EventProducer;
    private final AlertTriggerRepository alertTriggerRepository;

    public void sendNewC2AlertEvent(final CrmTriggerLog crmTriggerLog) {
        log.info("[C2AlertEventService][sendNew] CrmTriggerLog id={} eventDate={} eventTime={} (offset={}) triggerId={} servicePlatformId={} customerId={}",
                crmTriggerLog.getId(),
                crmTriggerLog.getEventDate(),
                crmTriggerLog.getEventTime(),
                crmTriggerLog.getEventTime() != null ? crmTriggerLog.getEventTime().getOffset() : null,
                crmTriggerLog.getTriggerId(),
                crmTriggerLog.getServicePlatform().getId(),
                crmTriggerLog.getCustomerId());
        List<AlertTriggerSeverity> alertTriggerSeverities =
                alertTriggerSeverityService.findByTriggerIdAndServicePlatformIdAndCustomerId(
                        crmTriggerLog.getTriggerId(),
                        crmTriggerLog.getServicePlatform().getId(),
                        crmTriggerLog.getCustomerId()
                );
        log.info("[C2AlertEventService][sendNew] resolved {} severity rows for CrmTriggerLog id={}",
                alertTriggerSeverities.size(), crmTriggerLog.getId());
        alertTriggerSeverities.stream()
                .map(s -> buildC2AlertEvent(crmTriggerLog, s))
                .forEach(event -> {
                    log.info("[C2AlertEventService][publish] eventDate={} eventTime={} (offset={}) severity={} alertId={} triggerName={}",
                            event.getEventDate(), event.getEventTime(),
                            event.getEventTime() != null ? event.getEventTime().getOffset() : null,
                            event.getSeverity(), event.getAlertId(), event.getTriggerName());
                    c2EventProducer.publishC2Events(event);
                });
    }



    // Use when the AlertTrigger row must be looked up by its primary key directly.
    public void sendNewC2AlertEventWithOverrideSeverity(
        final CrmTriggerLog crmTriggerLog,
        final Severity severity,
        final Long alertTriggerId
    ) {
        AlertTrigger alertTrigger = alertTriggerRepository
            .findById(alertTriggerId)
            .orElseThrow(() -> new RuntimeException(
                "AlertTrigger not found with id " + alertTriggerId
            ));
        C2AlertEventDto event = C2AlertEventDto(crmTriggerLog, severity, alertTrigger);
    }

    private C2AlertEventDto C2AlertEventDto(CrmTriggerLog crmTriggerLog, Severity severity, AlertTrigger alertTrigger) {
        C2AlertEventDto event = C2AlertEventDto.builder()
            .crmTriggerLogId(crmTriggerLog.getId())
            .alertId(alertTrigger.getAlertId())
            .triggerId(alertTrigger.getTriggerId())
            .triggerName(crmTriggerLog.getTriggerName())
            .servicePlatformId(crmTriggerLog.getServicePlatform().getId())
            .servicePlatformName(crmTriggerLog.getServicePlatform().getCode().name())
            .eventTime(crmTriggerLog.getEventTime())
            .eventDate(crmTriggerLog.getEventDate())
            .longitude(crmTriggerLog.getLongitude())
            .latitude(crmTriggerLog.getLatitude())
            .operationSiteId(crmTriggerLog.getOperationSiteId())
            .customerId(crmTriggerLog.getCustomerId())
            .workforceId(crmTriggerLog.getWorkforceId())
            .severity(severity)
            .description(crmTriggerLog.getDescription())
            .locationId(crmTriggerLog.getLocationId())
            .build();
        c2EventProducer.publishC2Events(event);
        return event;
    }

    private C2AlertEventDto buildC2AlertEvent(
        final CrmTriggerLog crmTriggerLog,
        final AlertTriggerSeverity alertTriggerSeverity
    ) {
        AlertTrigger alertTrigger = alertTriggerSeverity.getAlertTrigger();
        return C2AlertEventDto.builder()
                .crmTriggerLogId(crmTriggerLog.getId())
                .alertId(alertTrigger.getAlertId())
                .triggerId(alertTrigger.getTriggerId())
                .triggerName(crmTriggerLog.getTriggerName())
                .servicePlatformId(crmTriggerLog.getServicePlatform().getId())
                .servicePlatformName(crmTriggerLog.getServicePlatform().getCode().name())
                .eventTime(crmTriggerLog.getEventTime())
                .eventDate(crmTriggerLog.getEventDate())
                .longitude(crmTriggerLog.getLongitude())
                .latitude(crmTriggerLog.getLatitude())
                .operationSiteId(crmTriggerLog.getOperationSiteId())
                .customerId(crmTriggerLog.getCustomerId())
                .workforceId(crmTriggerLog.getWorkforceId())
                .severity(alertTriggerSeverity.getSeverity())
                .description(crmTriggerLog.getDescription())
                .locationId(crmTriggerLog.getLocationId())
                .build();
    }
}
