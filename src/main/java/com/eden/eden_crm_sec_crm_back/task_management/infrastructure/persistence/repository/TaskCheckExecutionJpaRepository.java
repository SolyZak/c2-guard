package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskCheckExecutionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCheckExecutionJpaRepository extends JpaRepository<TaskCheckExecutionJpaEntity, Long> {

    List<TaskCheckExecutionJpaEntity> findAllByTaskExecutionId(Long taskExecutionId);
}
