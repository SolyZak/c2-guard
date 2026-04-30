package com.eden.eden_crm_sec_crm_back.task_management.domain.repository;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckComparison;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskCheckComparisonReportProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskCheckComparisonRepository {

    TaskCheckComparison save(TaskCheckComparison comparison);

    Optional<TaskCheckComparison> findById(Long id);

    Optional<TaskCheckComparison> findByTaskCheckExecutionId(Long taskCheckExecutionId);

    Optional<TaskCheckComparison> findByTaskCheckExecutionIdAndTaskLocationChecksImageId(
            Long taskCheckExecutionId, Long taskLocationChecksImageId);

    List<TaskCheckComparison> findAllByTaskCheckDefinitionId(Long taskCheckDefinitionId);

    List<TaskCheckComparisonReportProjection> findComparisonReport(
            Long customerId, OffsetDateTime fromDate, OffsetDateTime toDate);

    Page<TaskCheckComparisonReportProjection> findComparisonReportPaginated(
            Long customerId, OffsetDateTime fromDate, OffsetDateTime toDate,
            Long locationId, String taskName, Pageable pageable);
}