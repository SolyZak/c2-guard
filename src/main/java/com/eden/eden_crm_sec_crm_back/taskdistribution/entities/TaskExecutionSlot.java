package com.eden.eden_crm_sec_crm_back.taskdistribution.entities;

import com.eden.eden_crm_sec_crm_back.models.Customer;
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
@Table(name = "task_execution_slot")
public class TaskExecutionSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_distribution_id")
    private TaskDistribution taskDistribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_assignment_id")
    private TaskAssignment taskAssignment;

    @Column(name = "start_date_time")
    private OffsetDateTime startDateTime;

    @Column(name = "end_date_time")
    private OffsetDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskDistributionStatus status;

    @Column(name = "task_execution_id")
    private Long taskExecutionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
