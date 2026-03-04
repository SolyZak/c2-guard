package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.impl;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskLocationChecksImage;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskLocationChecksImageRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.mapper.TaskPersistenceMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskLocationChecksImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskLocationChecksImageRepositoryImpl implements TaskLocationChecksImageRepository {

    private final TaskLocationChecksImageJpaRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    @Override
    public TaskLocationChecksImage save(TaskLocationChecksImage taskLocationChecksImage) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(taskLocationChecksImage)));
    }

    @Override
    public Optional<TaskLocationChecksImage> findByLocationIdAndTaskCheckDefinitionId(Long locationId, Long taskCheckDefinitionId) {
        return jpaRepository.findByLocationIdAndTaskCheckDefinitionIdAndDeletedFalse(locationId, taskCheckDefinitionId)
                .map(mapper::toDomain);
    }

    @Override
    public void updateRefImage(Long locationId, Long taskCheckDefinitionId, String refImage) {
        jpaRepository.updateRefImage(locationId, taskCheckDefinitionId, refImage);
    }
}