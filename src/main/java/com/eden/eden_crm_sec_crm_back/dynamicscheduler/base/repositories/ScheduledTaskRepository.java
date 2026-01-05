package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTaskEntity, UUID>, JpaSpecificationExecutor<ScheduledTaskEntity> {
    @Query("""
        SELECT DISTINCT t FROM ScheduledTaskEntity t
        LEFT JOIN FETCH t.executionLogs l
        WHERE t.isActive = true
        AND (
            (t.typeOfExecution = 'DATETIME'
                AND t.plannedExecutionTime >= :startDate
                AND t.plannedExecutionTime <= :endDate)
            OR (t.typeOfExecution = 'START_TIME_AND_DURATION'
                AND t.startDateTime <= :endDate)
            OR (t.typeOfExecution = 'CRON')
        )
    """)
    List<ScheduledTaskEntity> findAllActiveTasksInDateRange(
        @Param("startDate") OffsetDateTime startDate,
        @Param("endDate") OffsetDateTime endDate
    );

    @Query("""
        SELECT DISTINCT t FROM ScheduledTaskEntity t
        LEFT JOIN FETCH t.executionLogs l
        WHERE t.isActive = true
        AND t.taskType not in :loaders
        AND (
            (t.typeOfExecution = 'DATETIME'
                AND t.plannedExecutionTime >= :startDate
                AND t.plannedExecutionTime <= :endDate)
            OR (t.typeOfExecution = 'START_TIME_AND_DURATION'
                AND t.startDateTime <= :endDate)
            OR (t.typeOfExecution = 'CRON')
        )
    """)
    List<ScheduledTaskEntity> findAllActiveTasksInDateRangeExcludeLoaders(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("loaders") List<String> loaders
    );

    List<ScheduledTaskEntity> findAllByTaskType(String taskType);
}
