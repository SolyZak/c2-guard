package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskExecutionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskExecutionJpaRepository extends JpaRepository<TaskExecutionJpaEntity, Long> {
}
