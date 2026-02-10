package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.ServicePlatformWithTriggersResponse;
import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;
import com.eden.eden_crm_sec_crm_back.dto.TriggerWithAlertTriggerResponse;
import com.eden.eden_crm_sec_crm_back.entity.ServicePlatform;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TriggerMapper {

    @Mapping(target = "triggerId", source = "id")
    TriggerWithAlertTriggerResponse toTriggerWithAlertTriggerResponse(TriggerResponse triggerResponse);

    ServicePlatformWithTriggersResponse toServicePlatformWithTriggersResponse(ServicePlatform servicePlatform, List<TriggerWithAlertTriggerResponse> triggers);

}
