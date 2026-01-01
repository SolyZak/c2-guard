package com.eden.eden_crm_sec_crm_back.dynamicscheduler.dto;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.enums.TaskExecutionType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Duration;
import java.time.OffsetDateTime;

@Builder
@Valid
public record CreateScheduledTaskRequest(
        @NotBlank
        String name,

        @NotBlank
        String taskType,

        @NotNull
        TaskExecutionType typeOfExecution,

        String cronExpression,
        OffsetDateTime plannedExecutionTime,
        OffsetDateTime startDateTime,
        Duration duration,
        JsonNode arguments,

        Boolean isActive,

        OffsetDateTime createdAt
) {
    public CreateScheduledTaskRequest {
        if (createdAt == null)
            createdAt = OffsetDateTime.now();

        if (isActive == null)
            isActive = true;
    }

    @AssertTrue(message = "cronExpression must be not blank when typeOfExecution is CRON")
    private boolean isCronExpressionValid() {
        return typeOfExecution != TaskExecutionType.CRON || (cronExpression != null && !cronExpression.isBlank());
    }

    @AssertTrue(message = "plannedExecutionTime must be not null when typeOfExecution is DATETIME")
    private boolean isPlannedExecutionTimeValid() {
        return typeOfExecution != TaskExecutionType.DATETIME || plannedExecutionTime != null;
    }

    @AssertTrue(message = "startDateTime and duration must be not null when typeOfExecution is START_TIME_AND_DURATION")
    private boolean isStartDateTimeAndDurationValid() {
        return typeOfExecution != TaskExecutionType.START_TIME_AND_DURATION || (startDateTime != null && duration != null);
    }
}
