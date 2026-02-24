package com.eden.eden_crm_sec_crm_back.task_management.domain.repository;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckExecution;

import java.util.List;

public interface TaskCheckExecutionRepository {

    TaskCheckExecution save(TaskCheckExecution checkExecution);

    List<TaskCheckExecution> findAllByTaskExecutionId(Long taskExecutionId);
}
