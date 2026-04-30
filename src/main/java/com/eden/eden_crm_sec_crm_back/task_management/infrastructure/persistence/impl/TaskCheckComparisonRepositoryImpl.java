package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.impl;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckComparison;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckComparisonRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.mapper.TaskPersistenceMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskCheckComparisonJpaRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskCheckComparisonReportProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskCheckComparisonRepositoryImpl implements TaskCheckComparisonRepository {

    private final TaskCheckComparisonJpaRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public TaskCheckComparison save(TaskCheckComparison comparison) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(comparison)));
    }

    @Override
    public Optional<TaskCheckComparison> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TaskCheckComparison> findByTaskCheckExecutionId(Long taskCheckExecutionId) {
        return jpaRepository.findByTaskCheckExecutionId(taskCheckExecutionId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<TaskCheckComparison> findByTaskCheckExecutionIdAndTaskLocationChecksImageId(
            Long taskCheckExecutionId, Long taskLocationChecksImageId) {
        return jpaRepository.findByTaskCheckExecutionIdAndTaskLocationChecksImageId(
                        taskCheckExecutionId, taskLocationChecksImageId)
                .map(mapper::toDomain);
    }

    @Override
    public List<TaskCheckComparison> findAllByTaskCheckDefinitionId(Long taskCheckDefinitionId) {
        return jpaRepository.findAllByTaskCheckDefinitionId(taskCheckDefinitionId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TaskCheckComparisonReportProjection> findComparisonReport(
            Long customerId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        return jpaRepository.findComparisonReport(customerId, fromDate, toDate);
    }

    @Override
    public Page<TaskCheckComparisonReportProjection> findComparisonReportPaginated(
            Long customerId, OffsetDateTime fromDate, OffsetDateTime toDate,
            Long locationId, String taskName, Pageable pageable) {
        return jpaRepository.findComparisonReportPaginated(
                customerId, fromDate, toDate, locationId, taskName, pageable);
    }
}