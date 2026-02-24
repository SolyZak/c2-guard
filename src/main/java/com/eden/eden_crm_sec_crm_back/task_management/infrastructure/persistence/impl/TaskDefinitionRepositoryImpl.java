package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.impl;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskDefinitionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskCheckDefinitionJpaEntity;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskDefinitionJpaEntity;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.mapper.TaskPersistenceMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskCheckDefinitionJpaRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskDefinitionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskDefinitionRepositoryImpl implements TaskDefinitionRepository {

    private final TaskDefinitionJpaRepository jpaRepository;
    private final TaskCheckDefinitionJpaRepository checkJpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public TaskDefinition save(TaskDefinition taskDefinition) {
        TaskDefinitionJpaEntity entity = mapper.toJpaEntity(taskDefinition);
        TaskDefinitionJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TaskDefinition> findById(Long id) {
        return jpaRepository.findById(id).map(entity -> {
            List<TaskCheckDefinitionJpaEntity> checks =
                    checkJpaRepository.findAllByTaskDefinitionIdAndDeletedAtIsNull(entity.getId());
            return mapper.toDomain(entity, checks);
        });
    }

    @Override
    public List<TaskDefinition> findAllByCustomerId(Long customerId, int page, int size) {
        Page<TaskDefinitionJpaEntity> entityPage = jpaRepository.findAllByCustomerIdAndDeletedAtIsNull(
                customerId, PageRequest.of(page, size));
        return entityPage.getContent().stream().map(entity -> {
            List<TaskCheckDefinitionJpaEntity> checks =
                    checkJpaRepository.findAllByTaskDefinitionIdAndDeletedAtIsNull(entity.getId());
            return mapper.toDomain(entity, checks);
        }).collect(Collectors.toList());
    }

    @Override
    public long countByCustomerId(Long customerId) {
        return jpaRepository.countByCustomerIdAndDeletedAtIsNull(customerId);
    }

    @Override
    public List<TaskDefinition> findAllByCustomerId(Long customerId) {
        return jpaRepository.findAllByCustomerIdAndDeletedAtIsNull(customerId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        jpaRepository.softDelete(id, LocalDateTime.now());
    }
}
