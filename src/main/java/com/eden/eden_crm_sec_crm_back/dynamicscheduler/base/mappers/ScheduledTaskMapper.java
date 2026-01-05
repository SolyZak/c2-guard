package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.mappers;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.CronScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.DateTimeScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.StartTimeDurationScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScheduledTaskMapper {
    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    ScheduledTaskEntity cronTaskRequestToEntity(CronScheduledTaskRequest scheduledTaskRequest);

    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    ScheduledTaskEntity dateTimeTaskRequestToEntity(DateTimeScheduledTaskRequest scheduledTaskRequest);

    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    ScheduledTaskEntity startTimeDurationTaskRequestToEntity(StartTimeDurationScheduledTaskRequest scheduledTaskRequest);

    List<ScheduledTaskEntity> cronTaskRequestsToEntities(List<CronScheduledTaskRequest> scheduledTaskRequests);

    List<ScheduledTaskEntity> dateTimeTaskRequestsToEntities(List<DateTimeScheduledTaskRequest> scheduledTaskRequests);

    List<ScheduledTaskEntity> startTimeDurationTaskRequestsToEntities(List<StartTimeDurationScheduledTaskRequest> scheduledTaskRequests);

    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    CronScheduledTaskRequest entityToCronTaskRequest(ScheduledTaskEntity scheduledTaskEntity);

    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    DateTimeScheduledTaskRequest entityToDateTimeTaskRequest(ScheduledTaskEntity scheduledTaskEntity);

    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    StartTimeDurationScheduledTaskRequest entityToStartTimeDurationTaskRequest(ScheduledTaskEntity scheduledTaskEntity);

    List<CronScheduledTaskRequest> entitiesToCronTaskRequests(List<ScheduledTaskEntity> entityList);

    List<DateTimeScheduledTaskRequest> entitiesToDateTimeTaskRequests(List<ScheduledTaskEntity> entityList);

    List<StartTimeDurationScheduledTaskRequest> entitiesToStartTimeDurationTaskRequests(List<ScheduledTaskEntity> entityList);

    @Named("jsonNodeHandleNull")
    default JsonNode jsonNodeHandleNull(JsonNode arguments) {
        return arguments == null ? JsonNodeFactory.instance.objectNode() : arguments;
    }
}
