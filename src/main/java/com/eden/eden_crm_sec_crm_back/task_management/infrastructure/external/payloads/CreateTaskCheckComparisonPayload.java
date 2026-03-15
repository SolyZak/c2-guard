package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTaskCheckComparisonPayload {
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Long customerId;
    private Long locationId;
    private String evidenceImagePath;
}