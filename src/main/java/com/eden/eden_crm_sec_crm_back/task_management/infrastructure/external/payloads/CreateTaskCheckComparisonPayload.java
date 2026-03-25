package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateTaskCheckComparisonPayload {
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Long customerId;
    private Long locationId;
    private String evidenceImagePath;
    private List<ImageQualityIssue> missingQuality;
}