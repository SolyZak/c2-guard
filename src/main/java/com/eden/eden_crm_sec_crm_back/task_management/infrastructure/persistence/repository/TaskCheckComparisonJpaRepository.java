package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskCheckComparisonJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskCheckComparisonJpaRepository extends JpaRepository<TaskCheckComparisonJpaEntity, Long> {

    Optional<TaskCheckComparisonJpaEntity> findByTaskCheckExecutionId(Long taskCheckExecutionId);

    List<TaskCheckComparisonJpaEntity> findAllByTaskCheckDefinitionId(Long taskCheckDefinitionId);

    @Query(value = """
            SELECT DISTINCT
                tcc.id AS comparisonId,
                tcc.created_date AS comparisonDate,
                tcc.ratio AS comparisonRatio,
                tcc.matching AS matching,
                td.id AS taskDefinitionId,
                td.name AS taskDefinitionName,
                tcd.id AS checkDefinitionId,
                tcd.name AS checkDefinitionName,
                tlci.ref_image AS checkBaseImagePath,
                tce.id AS checkExecutionId,
                tce.evidence_image_path AS checkTransactionImagePath,
                te.workforce_id AS workforceId,
                l.id AS locationId,
                l.name AS locationName
            FROM task_check_comparison tcc
            JOIN task_check_definition tcd ON tcd.id = tcc.task_check_definition_id
            JOIN task_definition td ON td.id = tcd.task_definition_id
            JOIN task_check_execution tce ON tce.id = tcc.task_check_execution_id
            JOIN task_execution te ON te.id = tce.task_execution_id
            JOIN patrol_detail pd ON pd.task_definition_id = td.id
            JOIN location l ON l.id = pd.location_id
            LEFT JOIN task_location_checks_image tlci
                ON tlci.task_check_definition_id = tcd.id
                AND tlci.location_id = l.id
                AND tlci.deleted = false
            WHERE tcc.customer_id = :customerId
              AND tcc.created_date >= :fromDate
              AND tcc.created_date <= :toDate
              AND tcc.matching IS NULL
            ORDER BY tcc.created_date DESC
            """, nativeQuery = true)
    List<TaskCheckComparisonReportProjection> findComparisonReport(
            @Param("customerId") Long customerId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query(value = """
            SELECT DISTINCT
                tcc.id AS comparisonId,
                tcc.created_date AS comparisonDate,
                tcc.ratio AS comparisonRatio,
                tcc.matching AS matching,
                td.id AS taskDefinitionId,
                td.name AS taskDefinitionName,
                tcd.id AS checkDefinitionId,
                tcd.name AS checkDefinitionName,
                tlci.ref_image AS checkBaseImagePath,
                tce.id AS checkExecutionId,
                tce.evidence_image_path AS checkTransactionImagePath,
                te.workforce_id AS workforceId,
                l.id AS locationId,
                l.name AS locationName
            FROM task_check_comparison tcc
            JOIN task_check_definition tcd ON tcd.id = tcc.task_check_definition_id
            JOIN task_definition td ON td.id = tcd.task_definition_id
            JOIN task_check_execution tce ON tce.id = tcc.task_check_execution_id
            JOIN task_execution te ON te.id = tce.task_execution_id
            JOIN patrol_detail pd ON pd.task_definition_id = td.id
            JOIN location l ON l.id = pd.location_id
            LEFT JOIN task_location_checks_image tlci
                ON tlci.task_check_definition_id = tcd.id
                AND tlci.location_id = l.id
                AND tlci.deleted = false
            WHERE tcc.customer_id = :customerId
              AND tcc.created_date >= :fromDate
              AND tcc.created_date <= :toDate
              AND tcc.matching IS NULL
              AND (:locationId IS NULL OR l.id = :locationId)
              AND (:taskName IS NULL OR LOWER(td.name) LIKE LOWER(CONCAT('%', :taskName, '%')))
            """,
            countQuery = """
            SELECT COUNT(DISTINCT tcc.id)
            FROM task_check_comparison tcc
            JOIN task_check_definition tcd ON tcd.id = tcc.task_check_definition_id
            JOIN task_definition td ON td.id = tcd.task_definition_id
            JOIN task_check_execution tce ON tce.id = tcc.task_check_execution_id
            JOIN task_execution te ON te.id = tce.task_execution_id
            JOIN patrol_detail pd ON pd.task_definition_id = td.id
            JOIN location l ON l.id = pd.location_id
            LEFT JOIN task_location_checks_image tlci
                ON tlci.task_check_definition_id = tcd.id
                AND tlci.location_id = l.id
                AND tlci.deleted = false
            WHERE tcc.customer_id = :customerId
              AND tcc.created_date >= :fromDate
              AND tcc.created_date <= :toDate
              AND tcc.matching IS NULL
              AND (:locationId IS NULL OR l.id = :locationId)
              AND (:taskName IS NULL OR LOWER(td.name) LIKE LOWER(CONCAT('%', :taskName, '%')))
            """,
            nativeQuery = true)
    Page<TaskCheckComparisonReportProjection> findComparisonReportPaginated(
            @Param("customerId") Long customerId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("locationId") Long locationId,
            @Param("taskName") String taskName,
            Pageable pageable);
}