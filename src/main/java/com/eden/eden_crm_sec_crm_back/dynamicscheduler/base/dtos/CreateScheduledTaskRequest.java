package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public abstract class CreateScheduledTaskRequest {
    @NotBlank
    protected String name;

    @NotBlank
    protected String taskType;

    @NotNull
    protected TaskExecutionType typeOfExecution;

    @NotNull
    protected JsonNode arguments;

    @NotNull
    protected Boolean isActive;

    @NotNull
    protected OffsetDateTime createdAt;

    protected String description;

    protected Boolean isExecutionFinished;

    protected CreateScheduledTaskRequest(
        String name,
        String taskType,
        TaskExecutionType typeOfExecution,
        JsonNode arguments,
        Boolean isActive,
        OffsetDateTime createdAt,
        String description
    ) {
        this.name = name;
        this.taskType = taskType;
        this.typeOfExecution = typeOfExecution;
        this.arguments = arguments == null ? JsonNodeFactory.instance.objectNode() : arguments;
        this.isActive = isActive == null || isActive;
        this.createdAt = createdAt == null ? OffsetDateTime.now() : createdAt;
        this.description = description;
        this.isExecutionFinished = false;
    }
}
