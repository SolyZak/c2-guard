package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import lombok.Data;

import java.util.List;

@Data
public class CreateTaskCheckComparisonRequest {
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Long customerId;
    private Long locationId;
    private String evidenceImagePath;
    private List<ImageQualityIssue> missingQuality;
}