package com.eden.eden_crm_sec_crm_back.dynamicscheduler.mapper;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.dto.CreateScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
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
    ScheduledTaskEntity createRequestToEntity(CreateScheduledTaskRequest scheduledTaskRequest);

    List<ScheduledTaskEntity> createRequestsToEntities(List<CreateScheduledTaskRequest> scheduledTaskRequests);

    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    CreateScheduledTaskRequest entityToCreateRequest(ScheduledTaskEntity scheduledTaskEntity);

    List<CreateScheduledTaskRequest> entitiesToCreateRequests(List<ScheduledTaskEntity> entityList);

    @Named("jsonNodeHandleNull")
    default JsonNode jsonNodeHandleNull(JsonNode arguments) {
        return arguments == null ? JsonNodeFactory.instance.objectNode() : arguments;
    }
}
