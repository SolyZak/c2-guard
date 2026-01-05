package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class StartTimeDurationScheduledTaskRequest extends CreateScheduledTaskRequest {

    @NotNull
    private OffsetDateTime startDateTime;

    @NotNull
    private Duration duration;

    public StartTimeDurationScheduledTaskRequest(Builder builder) {
        super(builder.name, builder.taskType, TaskExecutionType.START_TIME_AND_DURATION, builder.arguments, builder.isActive, builder.createdAt);
        this.startDateTime = builder.startDateTime;
        this.duration = builder.duration;
    }

    @AssertTrue(message = "startDateTime and duration must be not null when typeOfExecution is START_TIME_AND_DURATION")
    private boolean isStartDateTimeAndDurationValid() {
        return typeOfExecution != TaskExecutionType.START_TIME_AND_DURATION || (startDateTime != null && duration != null);
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
        private OffsetDateTime startDateTime;
        private Duration duration;

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

        public Builder startDateTime(OffsetDateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public StartTimeDurationScheduledTaskRequest build() {
            return new StartTimeDurationScheduledTaskRequest(this);
        }
    }
}
