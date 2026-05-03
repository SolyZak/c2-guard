package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskCheckExecutionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskCheckExecutionJpaRepository extends JpaRepository<TaskCheckExecutionJpaEntity, Long> {

    List<TaskCheckExecutionJpaEntity> findAllByTaskExecutionId(Long taskExecutionId);

    @Query(value = """
        SELECT tce.id AS id, tcd.name AS checkName, CAST(tce.check_values AS text) AS checkValues,
               tce.evidence_image_path AS evidenceImagePath, tce.created_at AS createdAt
        FROM task_check_execution tce
        JOIN task_check_definition tcd ON tcd.id = tce.task_check_definition_id
        WHERE tce.task_execution_id = :taskExecutionId
        ORDER BY tce.id ASC
        """, nativeQuery = true)
    List<TaskCheckExecutionDetailProjection> findDetailsByTaskExecutionId(@Param("taskExecutionId") Long taskExecutionId);
}
