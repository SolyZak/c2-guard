package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskCheckComparisonJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskCheckComparisonJpaRepository extends JpaRepository<TaskCheckComparisonJpaEntity, Long> {

    Optional<TaskCheckComparisonJpaEntity> findByTaskCheckExecutionId(Long taskCheckExecutionId);

    List<TaskCheckComparisonJpaEntity> findAllByTaskCheckDefinitionId(Long taskCheckDefinitionId);
}