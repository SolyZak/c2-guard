package com.eden.eden_crm_sec_crm_back.task_management.domain.repository;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckDefinition;

import java.util.List;
import java.util.Optional;

public interface TaskCheckDefinitionRepository {

    TaskCheckDefinition save(TaskCheckDefinition checkDefinition);

    Optional<TaskCheckDefinition> findById(Long id);

    List<TaskCheckDefinition> findAllByTaskDefinitionId(Long taskDefinitionId);

    <T> List<T> findAllChecksByPremiseAndCustomer(Long premiseId, Long customerId, Class<T> projectionType);

}
