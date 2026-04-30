package com.eden.eden_crm_sec_crm_back.task_management.domain.model;

import com.eden.eden_crm_sec_crm_back.task_management.domain.event.TaskExecutionCreatedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskExecution {

    private Long id;
    private Long workforceId;
    private Long customerId;
    private LocalDateTime createdAt;
    private final List<TaskExecutionCreatedEvent> domainEvents = new ArrayList<>();

    private TaskExecution() {
    }

    public static TaskExecution create(Long workforceId, Long customerId) {
        TaskExecution execution = new TaskExecution();
        execution.workforceId = workforceId;
        execution.customerId = customerId;
        execution.createdAt = LocalDateTime.now();
        execution.domainEvents.add(new TaskExecutionCreatedEvent(null, workforceId, customerId, execution.createdAt));
        return execution;
    }

    public static TaskExecution reconstitute(Long id, Long workforceId, Long customerId, LocalDateTime createdAt) {
        TaskExecution execution = new TaskExecution();
        execution.id = id;
        execution.workforceId = workforceId;
        execution.customerId = customerId;
        execution.createdAt = createdAt;
        return execution;
    }

    public List<TaskExecutionCreatedEvent> pullDomainEvents() {
        List<TaskExecutionCreatedEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkforceId() { return workforceId; }
    public Long getCustomerId() { return customerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
