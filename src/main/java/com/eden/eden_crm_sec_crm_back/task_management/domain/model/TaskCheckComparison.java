package com.eden.eden_crm_sec_crm_back.task_management.domain.model;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
public class TaskCheckComparison {

    private Long id;
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Long taskLocationChecksImageId;
    private Boolean matching;
    private Double ratio;
    private Long customerId;
    private OffsetDateTime createdDate;
    private List<ImageQualityIssue> missingQuality;

    private TaskCheckComparison() {
    }

    public static TaskCheckComparison create(Long taskCheckDefinitionId, Long taskCheckExecutionId,
                                             Boolean matching, Double ratio, Long customerId,
                                             Long taskLocationChecksImageId,
                                             List<ImageQualityIssue> missingQuality) {  // NEW parameter
        TaskCheckComparison comparison = new TaskCheckComparison();
        comparison.taskCheckDefinitionId = taskCheckDefinitionId;
        comparison.taskCheckExecutionId = taskCheckExecutionId;
        comparison.taskLocationChecksImageId = taskLocationChecksImageId;
        comparison.matching = matching;
        comparison.ratio = ratio;
        comparison.customerId = customerId;
        comparison.createdDate = OffsetDateTime.now();
        comparison.missingQuality = missingQuality;
        return comparison;
    }

    public static TaskCheckComparison reconstitute(Long id, Long taskCheckDefinitionId, Long taskCheckExecutionId,
                                                   Boolean matching, Double ratio, Long customerId,
                                                   OffsetDateTime createdDate,
                                                   Long taskLocationChecksImageId,
                                                   List<ImageQualityIssue> missingQuality) {  // NEW parameter
        TaskCheckComparison comparison = new TaskCheckComparison();
        comparison.id = id;
        comparison.taskCheckDefinitionId = taskCheckDefinitionId;
        comparison.taskCheckExecutionId = taskCheckExecutionId;
        comparison.taskLocationChecksImageId = taskLocationChecksImageId;
        comparison.matching = matching;
        comparison.ratio = ratio;
        comparison.customerId = customerId;
        comparison.createdDate = createdDate;
        comparison.missingQuality = missingQuality;
        return comparison;
    }

    public void updateMatching(Boolean matching) {
        this.matching = matching;
    }

    public void updateRatio(Double ratio) {
        this.ratio = ratio;
    }
}