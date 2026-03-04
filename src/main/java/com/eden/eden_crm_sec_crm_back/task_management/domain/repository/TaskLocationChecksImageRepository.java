package com.eden.eden_crm_sec_crm_back.task_management.domain.repository;

import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskLocationChecksImage;

import java.util.Optional;

public interface TaskLocationChecksImageRepository {

    TaskLocationChecksImage save(TaskLocationChecksImage taskLocationChecksImage);

    Optional<TaskLocationChecksImage> findByLocationIdAndTaskCheckDefinitionId(Long locationId, Long taskCheckDefinitionId);

    void updateRefImage(Long locationId, Long taskCheckDefinitionId, String refImage);
}