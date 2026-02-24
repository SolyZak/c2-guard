package com.eden.eden_crm_sec_crm_back.task_management.domain.model;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.Severity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDefinition {

    private Long id;
    private String name;
    private Severity severity;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<TaskCheckDefinition> checks;

    private TaskDefinition() {
        this.checks = new ArrayList<>();
    }

    public static TaskDefinition create(String name, Severity severity, Long customerId) {
        TaskDefinition task = new TaskDefinition();
        task.name = name;
        task.severity = severity;
        task.customerId = customerId;
        task.createdAt = LocalDateTime.now();
        return task;
    }

    public static TaskDefinition reconstitute(Long id, String name, Severity severity, Long customerId,
                                              LocalDateTime createdAt, LocalDateTime updatedAt,
                                              LocalDateTime deletedAt, List<TaskCheckDefinition> checks) {
        TaskDefinition task = new TaskDefinition();
        task.id = id;
        task.name = name;
        task.severity = severity;
        task.customerId = customerId;
        task.createdAt = createdAt;
        task.updatedAt = updatedAt;
        task.deletedAt = deletedAt;
        task.checks = checks != null ? checks : new ArrayList<>();
        return task;
    }

    public void addCheck(TaskCheckDefinition check) {
        this.checks.add(check);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public Severity getSeverity() { return severity; }
    public Long getCustomerId() { return customerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public List<TaskCheckDefinition> getChecks() { return checks; }
}
