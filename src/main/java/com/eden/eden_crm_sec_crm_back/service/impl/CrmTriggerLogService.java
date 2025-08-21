package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.ServicePlatform;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import com.eden.eden_crm_sec_crm_back.repository.CrmTriggerLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrmTriggerLogService {
    private final CrmTriggerLogRepository crmTriggerLogRepository;
    private final ServicePlatformService servicePlatformService;

    @Transactional
    public CrmTriggerLog addNewCrmTriggerLog(final TriggerEventDto triggerEventDto) {
        final ServicePlatformEnum servicePlatformEnum = ServicePlatformEnum.fromCode(triggerEventDto.getServicePlatformName());
        final ServicePlatform servicePlatformById = servicePlatformService.getServicePlatformById(servicePlatformEnum.getId());
        final CrmTriggerLog crmTriggerLog = new CrmTriggerLog();
        crmTriggerLog.setServiceTriggerEventId(triggerEventDto.getServiceTriggerEventId());
        crmTriggerLog.setTriggerId(triggerEventDto.getTriggerId());
        crmTriggerLog.setTriggerName(triggerEventDto.getTriggerName());
        crmTriggerLog.setWorkforceId(triggerEventDto.getWorkforceId());
        crmTriggerLog.setOperationSiteId(triggerEventDto.getOperationSiteId());
        crmTriggerLog.setCustomerId(triggerEventDto.getCustomerId());
        crmTriggerLog.setLongitude(triggerEventDto.getLongitude());
        crmTriggerLog.setLatitude(triggerEventDto.getLatitude());
        crmTriggerLog.setEventTime(triggerEventDto.getEventTime());
        crmTriggerLog.setEventDate(triggerEventDto.getEventDate());
        crmTriggerLog.setDescription(triggerEventDto.getDescription());
        crmTriggerLog.setServicePlatform(servicePlatformById);
        return crmTriggerLogRepository.save(crmTriggerLog);
    }
}
