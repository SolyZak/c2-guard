package com.eden.eden_crm_sec_crm_back.task_management.domain.repository;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckComparison;

import java.util.List;
import java.util.Optional;

public interface TaskCheckComparisonRepository {

    TaskCheckComparison save(TaskCheckComparison comparison);

    Optional<TaskCheckComparison> findByTaskCheckExecutionId(Long taskCheckExecutionId);

    List<TaskCheckComparison> findAllByTaskCheckDefinitionId(Long taskCheckDefinitionId);
}