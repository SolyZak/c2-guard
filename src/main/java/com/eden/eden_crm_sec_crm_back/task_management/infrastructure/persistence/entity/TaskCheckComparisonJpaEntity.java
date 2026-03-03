package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_check_comparison")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCheckComparisonJpaEntity {

    @Id
    @SequenceGenerator(name = "task_check_comparison_id_seq", sequenceName = "task_check_comparison_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_check_comparison_id_seq")
    private Long id;

    @Column(name = "task_check_definition_id", nullable = false)
    private Long taskCheckDefinitionId;

    @Column(name = "task_check_execution_id", nullable = false)
    private Long taskCheckExecutionId;

    @Column(name = "matching", nullable = false)
    private Boolean matching;

    @Column(name = "ratio")
    private Double ratio;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
}