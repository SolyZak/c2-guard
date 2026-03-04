package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskLocationChecksImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TaskLocationChecksImageJpaRepository extends JpaRepository<TaskLocationChecksImageJpaEntity, Long> {

    boolean existsByLocationIdAndTaskCheckDefinitionIdAndDeletedFalse(Long locationId, Long taskCheckDefinitionId);

    Optional<TaskLocationChecksImageJpaEntity> findByLocationIdAndTaskCheckDefinitionIdAndDeletedFalse(
            Long locationId, Long taskCheckDefinitionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE TaskLocationChecksImageJpaEntity e SET e.refImage = :refImage WHERE e.locationId = :locationId AND e.taskCheckDefinitionId = :taskCheckDefinitionId AND e.deleted = false")
    void updateRefImage(@Param("locationId") Long locationId,
                        @Param("taskCheckDefinitionId") Long taskCheckDefinitionId,
                        @Param("refImage") String refImage);
}