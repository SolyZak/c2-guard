package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TaskCheckComparisonResponse {
    private Long id;
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Long taskLocationChecksImageId;
    private Boolean matching;
    private Double ratio;
    private Long customerId;
    private OffsetDateTime createdDate;
}