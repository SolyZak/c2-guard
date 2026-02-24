package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_execution")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionJpaEntity {

    @Id
    @SequenceGenerator(name = "task_execution_id_seq", sequenceName = "task_execution_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_execution_id_seq")
    private Long id;

    @Column(name = "workforce_id", nullable = false)
    private Long workforceId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
