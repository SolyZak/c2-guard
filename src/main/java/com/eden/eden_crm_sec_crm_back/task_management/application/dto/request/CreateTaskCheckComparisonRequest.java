package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import lombok.Data;

@Data
public class CreateTaskCheckComparisonRequest {
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Long customerId;
}