package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskCheckDefinitionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskCheckDefinitionJpaRepository extends JpaRepository<TaskCheckDefinitionJpaEntity, Long> {

    @Query("SELECT c FROM TaskCheckDefinitionJpaEntity c WHERE c.taskDefinitionId = :taskDefinitionId AND c.deletedAt IS NULL")
    List<TaskCheckDefinitionJpaEntity> findAllByTaskDefinitionIdAndDeletedAtIsNull(@Param("taskDefinitionId") Long taskDefinitionId);
}
