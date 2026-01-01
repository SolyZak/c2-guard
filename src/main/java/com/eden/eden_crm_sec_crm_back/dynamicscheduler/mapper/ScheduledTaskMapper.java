package com.eden.eden_crm_sec_crm_back.dynamicscheduler.mapper;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.dto.CreateScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScheduledTaskMapper {
    ScheduledTaskEntity createRequestToEntity(@Valid CreateScheduledTaskRequest scheduledTaskRequest);
}
