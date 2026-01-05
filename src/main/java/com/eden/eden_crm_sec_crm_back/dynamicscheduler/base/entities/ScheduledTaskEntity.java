package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.converters.DurationAttributeConverter;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.generators.uuid.UUIDv7;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "scheduled_tasks", indexes = {
        @Index(name = "idx_scheduled_tasks_id", columnList = "id"),
        @Index(name = "idx_scheduled_tasks_name", columnList = "name"),
        @Index(name = "idx_scheduled_tasks_task_type", columnList = "taskType"),
        @Index(name = "idx_scheduled_tasks_is_active", columnList = "isActive"),
        @Index(name = "idx_scheduled_tasks_type_active", columnList = "taskType,isActive"),
        @Index(name = "idx_scheduled_tasks_type_of_execution", columnList = "typeOfExecution"),
        @Index(name = "idx_scheduled_tasks_active_planned_execution_time", columnList = "isActive,plannedExecutionTime"),
        @Index(name = "idx_scheduled_tasks_created_at", columnList = "createdAt")
})
public class ScheduledTaskEntity {

    @Id
    @GeneratedValue
    @UUIDv7
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String taskType;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_execution", nullable = false)
    private TaskExecutionType typeOfExecution;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "planned_execution_time")
    private OffsetDateTime plannedExecutionTime;

    @Column(name = "start_date_time")
    private OffsetDateTime startDateTime;

    @Column(name = "duration")
    @Convert(converter = DurationAttributeConverter.class)
    private Duration duration;

    @Column(name = "arguments", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode arguments;

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true")
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<ScheduledTaskExecutionLogEntity> executionLogs = new ArrayList<>();
}
