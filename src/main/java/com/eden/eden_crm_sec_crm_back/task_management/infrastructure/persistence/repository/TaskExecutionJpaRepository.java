package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskExecutionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TaskExecutionJpaRepository extends JpaRepository<TaskExecutionJpaEntity, Long> {


    @Query("SELECT COUNT(te) FROM TaskExecutionJpaEntity te " +
            "WHERE te.workforceId = :workforceId " +
            "AND te.createdAt >= :startOfDay " +
            "AND te.createdAt < :startOfNextDay")
    long countByWorkforceIdAndCreatedAtBetween(
            @Param("workforceId") Long workforceId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay
    );
}