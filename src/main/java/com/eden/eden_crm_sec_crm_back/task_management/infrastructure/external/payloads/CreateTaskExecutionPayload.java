package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTaskExecutionPayload {
    private Long workforceId;
    private Long customerId;
}
