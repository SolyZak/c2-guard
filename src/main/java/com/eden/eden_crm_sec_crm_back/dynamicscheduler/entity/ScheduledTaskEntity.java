package com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.converter.DurationAttributeConverter;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.converter.JsonNodeAttributeConverter;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.enums.TaskExecutionType;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.uuid.UUIDv7;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "scheduled_tasks", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_task_type", columnList = "taskType"),
        @Index(name = "idx_active", columnList = "isActive"),
        @Index(name = "idx_task_type_active", columnList = "taskType,isActive"),
        @Index(name = "idx_type_of_execution", columnList = "typeOfExecution"),
        @Index(name = "idx_active_planned_execution_time", columnList = "isActive,plannedExecutionTime"),
        @Index(name = "idx_created_at", columnList = "createdAt")
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

    @Column(name = "arguments")
    @Convert(converter = JsonNodeAttributeConverter.class)
    private JsonNode arguments;

    @Column(name = "is_active")
    @ColumnDefault("true")
    private Boolean isActive;

    @Column(name = "created_at")
    @CreatedDate
    private OffsetDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ScheduledTaskExecutionLogEntity> executionLogs;
}
