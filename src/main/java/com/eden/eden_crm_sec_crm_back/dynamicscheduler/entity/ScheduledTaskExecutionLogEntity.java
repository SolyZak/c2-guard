package com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.converter.JsonNodeAttributeConverter;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.enums.ScheduledTaskStatus;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.uuid.UUIDv7;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "scheduled_task_execution_logs", indexes = {
        @Index(name = "idx_scheduled_task_execution_logs_id", columnList = "id"),
        @Index(name = "idx_scheduled_task_execution_logs_task_id", columnList = "task_id"),
        @Index(name = "idx_scheduled_task_execution_logs_started_at", columnList = "startedAt"),
        @Index(name = "idx_scheduled_task_execution_logs_finished_at", columnList = "finishedAt"),
        @Index(name = "idx_scheduled_task_execution_logs_status", columnList = "status")
})
public class ScheduledTaskExecutionLogEntity {

    @Id
    @GeneratedValue
    @UUIDv7
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private ScheduledTaskEntity task;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @ColumnDefault("STARTED")
    private ScheduledTaskStatus status;

    @Column(name = "result")
    @Convert(converter = JsonNodeAttributeConverter.class)
    private JsonNode result;
}
