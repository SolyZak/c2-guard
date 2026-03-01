package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.DistributableTaskProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatrolDetailRepository extends JpaRepository<PatrolDetail,Long> {
    // ─── [TASK-MIGRATION] COEXISTENCE ──────────────────────────────────────────
    // Native query: JPQL cannot LEFT JOIN unrelated entities, so we join
    // task_definition directly via the plain task_definition_id column.
    // COALESCE(t.name, td.name) handles both old-path (task != null) and
    // new-path (taskDefinitionId != null) patrol details.
    // CLEANUP: after Phase E, remove the t LEFT JOIN and COALESCE — use td.name only.
    // ─── [TASK-MIGRATION] END COEXISTENCE ──────────────────────────────────────
    @Query(value = """
        SELECT
            pd.id                           AS patrolDetailId,
            COALESCE(t.name, td.name)       AS taskName
        FROM patrol_detail pd
        LEFT JOIN task t
            ON pd.task_id = t.id
        LEFT JOIN task_definition td
            ON pd.task_definition_id = td.id
        LEFT JOIN patrol_task_distribution ptd
            ON ptd.patrol_detail_id = pd.id
            AND ptd.service_time_id = :serviceTimeId
        WHERE pd.patrol_id = :patrolId
          AND pd.location_id = :locationId
          AND (t.customer_id = :customerId OR td.customer_id = :customerId)
          AND ptd.id IS NULL
    """, nativeQuery = true)
    List<DistributableTaskProjection> getDistributableTasks(
        @Param("customerId") Long customerId,
        @Param("patrolId") Long patrolId,
        @Param("locationId") Long locationId,
        @Param("serviceTimeId") Long serviceTimeId
    );

    @Query(value = """
        SELECT td.name
        FROM patrol_detail pd
        JOIN task_definition td ON pd.task_definition_id = td.id
        WHERE pd.id = :detailId
    """, nativeQuery = true)
    List<String> getTaskDefinitionNamesByDetailId(@Param("detailId") Long detailId);

    long countByPatrol_Id(Long patrolId);
}
