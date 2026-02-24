package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import java.time.LocalDateTime;

public record TaskExecutionResponse(
        Long id,
        Long workforceId,
        Long customerId,
        LocalDateTime createdAt
) {
}
