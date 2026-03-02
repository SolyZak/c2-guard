package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.impl;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckDefinitionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.mapper.TaskPersistenceMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskCheckDefinitionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskCheckDefinitionRepositoryImpl implements TaskCheckDefinitionRepository {

    private final TaskCheckDefinitionJpaRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public TaskCheckDefinition save(TaskCheckDefinition checkDefinition) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(checkDefinition)));
    }

    @Override
    public Optional<TaskCheckDefinition> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TaskCheckDefinition> findAllByTaskDefinitionId(Long taskDefinitionId) {
        return jpaRepository.findAllByTaskDefinitionIdAndDeletedAtIsNull(taskDefinitionId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> findAllChecksByPremiseAndCustomer(Long premiseId, Long customerId, Class<T> projectionType) {
        return jpaRepository.findAllChecksByPremiseAndCustomer(premiseId, customerId, projectionType);
    }

    @Override
    public void updateImageUrl(Long id, String imageUrl) {
        jpaRepository.updateImageUrl(id, imageUrl);
    }
}
