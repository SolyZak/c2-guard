package com.eden.eden_crm_sec_crm_back.task_management.domain.event;

import java.time.LocalDateTime;

public record TaskExecutionCreatedEvent(
        Long executionId,
        Long workforceId,
        Long customerId,
        LocalDateTime occurredAt
) {
}
