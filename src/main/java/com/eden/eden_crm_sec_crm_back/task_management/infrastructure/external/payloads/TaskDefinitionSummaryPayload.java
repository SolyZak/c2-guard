package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDefinitionSummaryPayload {
    private Long id;
    private String name;
}
