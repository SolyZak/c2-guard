package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskDefinitionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskDefinitionJpaRepository extends JpaRepository<TaskDefinitionJpaEntity, Long> {

    @Query("SELECT t FROM TaskDefinitionJpaEntity t WHERE t.customerId = :customerId AND t.deletedAt IS NULL ORDER BY t.id DESC")
    Page<TaskDefinitionJpaEntity> findAllByCustomerIdAndDeletedAtIsNull(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT t FROM TaskDefinitionJpaEntity t WHERE t.customerId = :customerId AND t.deletedAt IS NULL ORDER BY t.id DESC")
    List<TaskDefinitionJpaEntity> findAllByCustomerIdAndDeletedAtIsNull(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(t) FROM TaskDefinitionJpaEntity t WHERE t.customerId = :customerId AND t.deletedAt IS NULL")
    long countByCustomerIdAndDeletedAtIsNull(@Param("customerId") Long customerId);

    @Modifying
    @Query("UPDATE TaskDefinitionJpaEntity t SET t.deletedAt = :deletedAt WHERE t.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);
}
