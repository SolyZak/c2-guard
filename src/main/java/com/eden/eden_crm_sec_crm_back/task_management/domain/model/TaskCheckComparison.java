package com.eden.eden_crm_sec_crm_back.task_management.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskCheckComparison {

    private Long id;
    private Long taskCheckDefinitionId;
    private Long taskCheckExecutionId;
    private Boolean matching;
    private Double ratio;
    private LocalDateTime createdDate;

    private TaskCheckComparison() {
    }

    public static TaskCheckComparison create(Long taskCheckDefinitionId, Long taskCheckExecutionId,
                                             Boolean matching, Double ratio) {
        TaskCheckComparison comparison = new TaskCheckComparison();
        comparison.taskCheckDefinitionId = taskCheckDefinitionId;
        comparison.taskCheckExecutionId = taskCheckExecutionId;
        comparison.matching = matching;
        comparison.ratio = ratio;
        comparison.createdDate = LocalDateTime.now();
        return comparison;
    }

    public static TaskCheckComparison reconstitute(Long id, Long taskCheckDefinitionId, Long taskCheckExecutionId,
                                                   Boolean matching, Double ratio, LocalDateTime createdDate) {
        TaskCheckComparison comparison = new TaskCheckComparison();
        comparison.id = id;
        comparison.taskCheckDefinitionId = taskCheckDefinitionId;
        comparison.taskCheckExecutionId = taskCheckExecutionId;
        comparison.matching = matching;
        comparison.ratio = ratio;
        comparison.createdDate = createdDate;
        return comparison;
    }
}