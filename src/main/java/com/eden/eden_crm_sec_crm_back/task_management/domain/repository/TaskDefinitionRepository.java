package com.eden.eden_crm_sec_crm_back.task_management.domain.repository;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskDefinition;

import java.util.List;
import java.util.Optional;

public interface TaskDefinitionRepository {

    TaskDefinition save(TaskDefinition taskDefinition);

    Optional<TaskDefinition> findById(Long id);

    List<TaskDefinition> findAllByCustomerId(Long customerId, int page, int size);

    long countByCustomerId(Long customerId);

    List<TaskDefinition> findAllByCustomerId(Long customerId);

    void softDelete(Long id);
}
