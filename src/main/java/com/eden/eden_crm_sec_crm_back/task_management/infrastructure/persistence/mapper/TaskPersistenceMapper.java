package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.mapper;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.*;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.CheckType;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.Severity;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class TaskPersistenceMapper {

    // ---- toJpaEntity (MapStruct generates using Lombok @Builder) ----

    public abstract TaskDefinitionJpaEntity toJpaEntity(TaskDefinition domain);

    @Mapping(target = "severity", source = "severity")
    @Mapping(target = "checkType", source = "checkType")
    public abstract TaskCheckDefinitionJpaEntity toJpaEntity(TaskCheckDefinition domain);

    public abstract TaskExecutionJpaEntity toJpaEntity(TaskExecution domain);

    @Mapping(target = "checkType", source = "checkType")
    public abstract TaskCheckExecutionJpaEntity toJpaEntity(TaskCheckExecution domain);

    public abstract TaskCheckComparisonJpaEntity toJpaEntity(TaskCheckComparison domain);

    public TaskLocationChecksImageJpaEntity toJpaEntity(TaskLocationChecksImage domain) {
        if (domain == null) return null;
        return TaskLocationChecksImageJpaEntity.builder()
                .id(domain.getId())
                .taskDefinitionId(domain.getTaskDefinitionId())
                .locationId(domain.getLocationId())
                .taskCheckDefinitionId(domain.getTaskCheckDefinitionId())
                .customerId(domain.getCustomerId())
                .refImage(domain.getRefImage())
                .deleted(domain.isDeleted())
                .createdBy(domain.getCreatedBy())
                .modifiedBy(domain.getModifiedBy())
                .createdDate(domain.getCreatedDate())
                .modifiedDate(domain.getModifiedDate())
                .build();
    }

    // ---- toDomain (manual — domain uses private constructors + static
    // reconstitute factories) ----

    public TaskDefinition toDomain(TaskDefinitionJpaEntity entity) {
        return toDomain(entity, Collections.emptyList());
    }

    public TaskDefinition toDomain(TaskDefinitionJpaEntity entity, List<TaskCheckDefinitionJpaEntity> checkEntities) {
        List<TaskCheckDefinition> checks = checkEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
        return TaskDefinition.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getSeverity() != null ? Severity.valueOf(entity.getSeverity()) : null,
                entity.getCustomerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                checks);
    }

    public TaskCheckDefinition toDomain(TaskCheckDefinitionJpaEntity entity) {
        return TaskCheckDefinition.reconstitute(
                entity.getId(),
                entity.getTaskDefinitionId(),
                entity.getName(),
                entity.getSeverity() != null ? Severity.valueOf(entity.getSeverity()) : null,
                entity.getCheckType() != null ? CheckType.valueOf(entity.getCheckType()) : null,
                entity.getCheckSettings(),
                entity.isHasEvidence(),
                entity.isHasComment(),
                entity.getCustomerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt());
    }

    public TaskExecution toDomain(TaskExecutionJpaEntity entity) {
        return TaskExecution.reconstitute(
                entity.getId(),
                entity.getWorkforceId(),
                entity.getCustomerId(),
                entity.getCreatedAt());
    }

    public TaskCheckExecution toDomain(TaskCheckExecutionJpaEntity entity) {
        return TaskCheckExecution.reconstitute(
                entity.getId(),
                entity.getTaskCheckDefinitionId(),
                entity.getTaskExecutionId(),
                entity.getCheckType() != null ? CheckType.valueOf(entity.getCheckType()) : null,
                entity.getCheckValues(),
                entity.getEvidenceImagePath(),
                entity.getComment(),
                entity.getCustomerId(),
                entity.getCreatedAt());
    }

    public TaskCheckComparison toDomain(TaskCheckComparisonJpaEntity entity) {
        return TaskCheckComparison.reconstitute(
                entity.getId(),
                entity.getTaskCheckDefinitionId(),
                entity.getTaskCheckExecutionId(),
                entity.getMatching(),
                entity.getRatio(),
                entity.getCustomerId(),
                entity.getCreatedDate());
    }

    public TaskLocationChecksImage toDomain(TaskLocationChecksImageJpaEntity entity) {
        if (entity == null) return null;
        return TaskLocationChecksImage.builder()
                .id(entity.getId())
                .taskDefinitionId(entity.getTaskDefinitionId())
                .locationId(entity.getLocationId())
                .taskCheckDefinitionId(entity.getTaskCheckDefinitionId())
                .customerId(entity.getCustomerId())
                .refImage(entity.getRefImage())
                .deleted(entity.isDeleted())
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdDate(entity.getCreatedDate())
                .modifiedDate(entity.getModifiedDate())
                .build();
    }
}