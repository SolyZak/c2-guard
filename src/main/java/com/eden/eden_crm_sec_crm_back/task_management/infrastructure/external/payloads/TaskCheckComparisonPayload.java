package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class TaskCheckComparisonPayload {
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