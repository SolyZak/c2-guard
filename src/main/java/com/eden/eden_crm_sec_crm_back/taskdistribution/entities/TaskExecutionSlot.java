package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "task_execution_slot", indexes = {
    @Index(name = "idx_task_execution_slot_task_distribution_id", columnList = "task_distribution_id"),
    @Index(name = "idx_task_execution_slot_task_assignment_id", columnList = "task_assignment_id"),
    @Index(name = "idx_task_execution_slot_customer_date_range", columnList = "customer_id, start_date_time, end_date_time"),
    @Index(name = "idx_task_execution_slot_status", columnList = "status"),
    @Index(name = "idx_task_execution_slot_executed_by_workforce_id", columnList = "executed_by_workforce_id"),
    // [TASK-MIGRATION] COEXISTENCE: remove after Phase E when task_execution_id column is dropped.
    @Index(name = "idx_task_execution_slot_task_execution_id", columnList = "task_execution_id"),
    // [TASK-MIGRATION] NEW: index for new module task execution linkage.
    @Index(name = "idx_task_execution_slot_new_task_execution_id", columnList = "new_task_execution_id"),
    @Index(name = "idx_task_execution_slot_status_created_at", columnList = "status, created_at"),
    @Index(name = "idx_task_execution_slot_workforce_status", columnList = "executed_by_workforce_id, status")
})
public class TaskExecutionSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_distribution_id", nullable = false)
    private TaskDistribution taskDistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_assignment_id", nullable = false)
    private TaskAssignment taskAssignment;

    @Column(name = "start_date_time", nullable = false)
    private OffsetDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private OffsetDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskDistributionStatus status;

    // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
    // Legacy FK to task_patrol_execution (old execution model).
    // CLEANUP: drop this field after Phase E; rename new_task_execution_id → task_execution_id.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_execution_id")
    private TaskPatrolExecution taskExecution;
    // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Plain Long FK to task_execution (new module) — no @ManyToOne for cross-module isolation.
    // CLEANUP: rename to task_execution_id after Phase E.
    @Column(name = "new_task_execution_id")
    private Long newTaskExecutionId;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    @Column(name = "executed_by_workforce_id")
    private Long executedByWorkforceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
