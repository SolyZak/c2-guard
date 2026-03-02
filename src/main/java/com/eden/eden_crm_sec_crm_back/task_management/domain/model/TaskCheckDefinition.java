package com.eden.eden_crm_sec_crm_back.task_management.domain.model;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.CheckType;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.Severity;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class TaskCheckDefinition {

    @Setter
    private Long id;
    private Long taskDefinitionId;
    private String name;
    private Severity severity;
    private CheckType checkType;
    private TaskCheckValue checkSettings;
    private boolean hasEvidence;
    private boolean hasComment;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Setter
    private String imageUrl;

    private TaskCheckDefinition() {
    }

    public static TaskCheckDefinition create(Long taskDefinitionId, String name, Severity severity,
            CheckType checkType, TaskCheckValue checkSettings,
            boolean hasEvidence, boolean hasComment, Long customerId) {
        TaskCheckDefinition check = new TaskCheckDefinition();
        check.taskDefinitionId = taskDefinitionId;
        check.name = name;
        check.severity = severity;
        check.checkType = checkType;
        check.checkSettings = checkSettings;
        check.hasEvidence = hasEvidence;
        check.hasComment = hasComment;
        check.customerId = customerId;
        check.createdAt = LocalDateTime.now();
        return check;
    }

    public static TaskCheckDefinition reconstitute(
            Long id, Long taskDefinitionId, String name, Severity severity,
            CheckType checkType, TaskCheckValue checkSettings,
            boolean hasEvidence, boolean hasComment, Long customerId,
            LocalDateTime createdAt, LocalDateTime updatedAt,
            LocalDateTime deletedAt, String imageUrl)
    {
        TaskCheckDefinition check = new TaskCheckDefinition();
        check.id = id;
        check.taskDefinitionId = taskDefinitionId;
        check.name = name;
        check.severity = severity;
        check.checkType = checkType;
        check.checkSettings = checkSettings;
        check.hasEvidence = hasEvidence;
        check.hasComment = hasComment;
        check.customerId = customerId;
        check.createdAt = createdAt;
        check.updatedAt = updatedAt;
        check.deletedAt = deletedAt;
        check.imageUrl = imageUrl;
        return check;
    }

}
