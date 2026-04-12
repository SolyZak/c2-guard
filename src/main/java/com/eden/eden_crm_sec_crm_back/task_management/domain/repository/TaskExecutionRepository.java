package com.eden.eden_crm_sec_crm_back.task_management.domain.repository;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskExecution;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskExecutionRepository {

    TaskExecution save(TaskExecution taskExecution);

    Optional<TaskExecution> findById(Long id);

    long countByWorkforceIdAndCreatedAtBetween(Long workforceId, LocalDateTime startOfDay, LocalDateTime startOfNextDay);
}