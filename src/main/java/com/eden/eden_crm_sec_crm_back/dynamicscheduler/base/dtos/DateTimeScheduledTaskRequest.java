package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class DateTimeScheduledTaskRequest extends CreateScheduledTaskRequest {

    @NotNull
    private OffsetDateTime plannedExecutionTime;

    public DateTimeScheduledTaskRequest(Builder builder) {
        super(
            builder.name,
            builder.taskType,
            TaskExecutionType.DATETIME,
            builder.arguments,
            builder.isActive,
            builder.createdAt,
            builder.description
        );
        this.plannedExecutionTime = builder.plannedExecutionTime;
    }

    @AssertTrue(message = "plannedExecutionTime must be not null when typeOfExecution is DATETIME")
    private boolean isPlannedExecutionTimeValid() {
        return typeOfExecution != TaskExecutionType.DATETIME || plannedExecutionTime != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String taskType;
        private JsonNode arguments;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime plannedExecutionTime;
        private String description;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder taskType(String taskType) {
            this.taskType = taskType;
            return this;
        }

        public Builder arguments(JsonNode arguments) {
            this.arguments = arguments;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder plannedExecutionTime(OffsetDateTime plannedExecutionTime) {
            this.plannedExecutionTime = plannedExecutionTime;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public DateTimeScheduledTaskRequest build() {
            return new DateTimeScheduledTaskRequest(this);
        }
    }
}
