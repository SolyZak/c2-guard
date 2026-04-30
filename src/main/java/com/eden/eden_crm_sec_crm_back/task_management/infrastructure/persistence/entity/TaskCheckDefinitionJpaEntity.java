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
@Table(name = "task_check_definition")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCheckDefinitionJpaEntity {

    @Id
    @SequenceGenerator(name = "task_check_definition_id_seq", sequenceName = "task_check_definition_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_check_definition_id_seq")
    private Long id;

    @Column(name = "task_definition_id", nullable = false)
    private Long taskDefinitionId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "check_type", nullable = false)
    private String checkType;

    @Column(name = "check_settings", nullable = false, columnDefinition = "jsonb")
    @Convert(converter = TaskCheckValueConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private TaskCheckValue checkSettings;

    @Column(name = "has_evidence", nullable = false)
    private boolean hasEvidence;

    @Column(name = "has_comment", nullable = false)
    private boolean hasComment;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}