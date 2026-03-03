package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCheckComparisonResponse {
    private Long id;
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Boolean matching;
    private Double ratio;
    private Long customerId;
    private LocalDateTime createdDate;
}