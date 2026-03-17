package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class TaskCheckComparisonReportResponse {
    private Long comparisonId;
    private OffsetDateTime comparisonDate;
    private Double comparisonRatio;
    private Boolean matching;
    private Long taskDefinitionId;
    private String taskDefinitionName;
    private Long checkDefinitionId;
    private String checkDefinitionName;
    private String checkBaseImage;
    private Long checkExecutionId;
    private String checkTransactionImage;
    private Long workforceId;
    private String workforceName;
    private Long locationId;
    private String locationName;
}