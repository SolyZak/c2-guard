package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.converter.TaskCheckValueConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_check_execution")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCheckExecutionJpaEntity {

    @Id
    @SequenceGenerator(name = "task_check_execution_id_seq", sequenceName = "task_check_execution_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_check_execution_id_seq")
    private Long id;

    @Column(name = "task_check_definition_id", nullable = false)
    private Long taskCheckDefinitionId;

    @Column(name = "task_execution_id", nullable = false)
    private Long taskExecutionId;

    @Column(name = "check_type", nullable = false)
    private String checkType;

    @Column(name = "check_values", nullable = false, columnDefinition = "jsonb")
    @Convert(converter = TaskCheckValueConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private TaskCheckValue checkValues;

    @Column(name = "evidence_image_path")
    private String evidenceImagePath;

    @Column(name = "comment")
    private String comment;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
