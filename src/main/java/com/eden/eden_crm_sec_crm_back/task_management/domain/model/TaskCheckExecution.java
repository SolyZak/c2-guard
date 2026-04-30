package com.eden.eden_crm_sec_crm_back.task_management.domain.model;

import com.eden.eden_crm_sec_crm_back.task_management.domain.event.TaskCheckExecutionSubmittedEvent;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.CheckType;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class TaskCheckExecution {

    private Long id;
    private Long taskCheckDefinitionId;
    private Long taskExecutionId;
    private CheckType checkType;
    private TaskCheckValue checkValues;
    private String evidenceImagePath;
    private String comment;
    private Long customerId;
    private LocalDateTime createdAt;
    private final List<TaskCheckExecutionSubmittedEvent> domainEvents = new ArrayList<>();

    private TaskCheckExecution() {
    }

    public static TaskCheckExecution submit(Long taskCheckDefinitionId, Long taskExecutionId,
            CheckType checkType, TaskCheckValue checkValues,
            String evidenceImagePath, String comment, Long customerId) {
        TaskCheckExecution execution = new TaskCheckExecution();
        execution.taskCheckDefinitionId = taskCheckDefinitionId;
        execution.taskExecutionId = taskExecutionId;
        execution.checkType = checkType;
        execution.checkValues = checkValues;
        execution.evidenceImagePath = evidenceImagePath;
        execution.comment = comment;
        execution.customerId = customerId;
        execution.createdAt = LocalDateTime.now();
        execution.domainEvents.add(new TaskCheckExecutionSubmittedEvent(null, taskExecutionId, execution.createdAt));
        return execution;
    }

    public static TaskCheckExecution reconstitute(Long id, Long taskCheckDefinitionId, Long taskExecutionId,
            CheckType checkType, TaskCheckValue checkValues,
            String evidenceImagePath, String comment,
            Long customerId, LocalDateTime createdAt) {
        TaskCheckExecution execution = new TaskCheckExecution();
        execution.id = id;
        execution.taskCheckDefinitionId = taskCheckDefinitionId;
        execution.taskExecutionId = taskExecutionId;
        execution.checkType = checkType;
        execution.checkValues = checkValues;
        execution.evidenceImagePath = evidenceImagePath;
        execution.comment = comment;
        execution.customerId = customerId;
        execution.createdAt = createdAt;
        return execution;
    }

    public List<TaskCheckExecutionSubmittedEvent> pullDomainEvents() {
        List<TaskCheckExecutionSubmittedEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
    }
}
