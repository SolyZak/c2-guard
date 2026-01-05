package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class CronScheduledTaskRequest extends CreateScheduledTaskRequest {

    @NotBlank
    private String cronExpression;

    public CronScheduledTaskRequest(Builder builder) {
        super(builder.name, builder.taskType, TaskExecutionType.CRON, builder.arguments, builder.isActive, builder.createdAt);
        this.cronExpression = builder.cronExpression;
    }

    @AssertTrue(message = "cronExpression must be not blank when typeOfExecution is CRON")
    private boolean isCronExpressionValid() {
        return typeOfExecution != TaskExecutionType.CRON || (cronExpression != null && !cronExpression.isBlank());
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
        private String cronExpression;

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

        public Builder cronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }

        public CronScheduledTaskRequest build() {
            return new CronScheduledTaskRequest(this);
        }
    }
}
