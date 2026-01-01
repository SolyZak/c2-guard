package com.eden.eden_crm_sec_crm_back.dynamicscheduler.mapper;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.dto.CreateScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ScheduledTaskMapper {
    @Mapping(target = "arguments", source = "arguments", qualifiedByName = "jsonNodeHandleNull")
    public abstract ScheduledTaskEntity createRequestToEntity(@Valid CreateScheduledTaskRequest scheduledTaskRequest);

    @Named("jsonNodeHandleNull")
    protected JsonNode jsonNodeHandleNull(JsonNode arguments) {
        return arguments == null ? JsonNodeFactory.instance.objectNode() : arguments;
    }
}
