package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskExecutionPayload {
    private Long id;
    private Long workforceId;
    private Long customerId;
    private LocalDateTime createdAt;
}
