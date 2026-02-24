package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "task_distribution", indexes = {
    @Index(name = "idx_task_distribution_contract_type", columnList = "contract_id, distribution_type"),
    @Index(name = "idx_task_distribution_customer_id", columnList = "customer_id"),
    // [TASK-MIGRATION] COEXISTENCE: remove this index entry after Phase E when task_id column is dropped.
    @Index(name = "idx_task_distribution_task_id", columnList = "task_id"),
    // [TASK-MIGRATION] NEW: index for new task_management integration column.
    @Index(name = "idx_task_distribution_task_definition_id", columnList = "task_definition_id"),
    @Index(name = "idx_task_distribution_created_at", columnList = "created_at"),
    @Index(name = "idx_task_distribution_distribution_type", columnList = "distribution_type"),
    @Index(name = "idx_task_distribution_customer_created", columnList = "customer_id, created_at"),
    @Index(name = "idx_task_distribution_contract_customer", columnList = "contract_id, customer_id")
})
public class TaskDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private CustomerContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
    // Legacy FK to old task table. nullable = true because new records created via
    // the taskDefinitionId path will leave this null.
    // CLEANUP: remove this @ManyToOne field entirely after Phase E (when task_id is dropped).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = true)
    private Task task;
    // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Plain Long FK — no @ManyToOne to task_management JPA entities to avoid cross-module JPA coupling.
    // CLEANUP: add NOT NULL constraint here (remove nullable = true) after Phase E migration confirms
    //          all rows in task_distribution have task_definition_id populated.
    @Column(name = "task_definition_id", nullable = true)
    private Long taskDefinitionId;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "distribution_type", nullable = false)
    private DistributionType distributionType;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @OneToOne(mappedBy = "taskDistribution", cascade = CascadeType.ALL, orphanRemoval = true)
    private ImmediateTaskDistribution immediateTaskDistribution;

    @OneToOne(mappedBy = "taskDistribution", cascade = CascadeType.ALL, orphanRemoval = true)
    private PatrolTaskDistribution patrolTaskDistribution;

    @OneToMany(mappedBy = "taskDistribution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskExecutionSlot> executionSlots;
}
