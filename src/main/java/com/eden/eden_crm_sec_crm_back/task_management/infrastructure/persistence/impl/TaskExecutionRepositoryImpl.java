package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.impl;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.mapper.TaskPersistenceMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskExecutionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskExecutionRepositoryImpl implements TaskExecutionRepository {

    private final TaskExecutionJpaRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public TaskExecution save(TaskExecution taskExecution) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(taskExecution)));
    }

    @Override
    public Optional<TaskExecution> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public long countByWorkforceIdAndCreatedAtBetween(Long workforceId, LocalDateTime startOfDay, LocalDateTime startOfNextDay) {
        return jpaRepository.countByWorkforceIdAndCreatedAtBetween(workforceId, startOfDay, startOfNextDay);
    }
}