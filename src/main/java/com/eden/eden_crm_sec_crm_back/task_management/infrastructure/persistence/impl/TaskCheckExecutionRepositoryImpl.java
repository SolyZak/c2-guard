package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.impl;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.mapper.TaskPersistenceMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskCheckExecutionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskCheckExecutionRepositoryImpl implements TaskCheckExecutionRepository {

    private final TaskCheckExecutionJpaRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public TaskCheckExecution save(TaskCheckExecution checkExecution) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(checkExecution)));
    }

    @Override
    public List<TaskCheckExecution> findAllByTaskExecutionId(Long taskExecutionId) {
        return jpaRepository.findAllByTaskExecutionId(taskExecutionId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
