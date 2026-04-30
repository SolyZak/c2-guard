package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

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
    private List<ImageQualityIssue> missingQuality;
}