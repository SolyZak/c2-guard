package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

import com.eden.eden_crm_sec_crm_back.models.Customer;
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
@Table(name = "task_assignment", indexes = {
    @Index(name = "idx_task_assignment_workforce_id", columnList = "workforce_id"),
    @Index(name = "idx_task_assignment_customer_id", columnList = "customer_id"),
    @Index(name = "idx_task_assignment_slot_number", columnList = "slot_number"),
    @Index(name = "idx_task_assignment_assigned_at", columnList = "assigned_at"),
    @Index(name = "idx_task_assignment_workforce_customer", columnList = "workforce_id, customer_id"),
    @Index(name = "idx_task_assignment_customer_assigned", columnList = "customer_id, assigned_at"),
    @Index(name = "idx_task_assignment_workforce_slot", columnList = "workforce_id, slot_number")
})
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number")
    private Integer slotNumber;

    @Column(name = "workforce_id")
    private Long workforceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "assigned_at")
    private OffsetDateTime assignedAt;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
