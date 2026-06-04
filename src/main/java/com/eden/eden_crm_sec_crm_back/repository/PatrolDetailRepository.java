package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.DistributableTaskProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatrolDetailRepository extends JpaRepository<PatrolDetail,Long> {

    /**
     * Active PatrolDetail rows for a specific (patrol, location, taskDefinition).
     * <p>A PatrolDetail is per-task, so (patrol, location) alone is NOT unique — it
     * matches one row per task at that location. The assignment-edit ADD path must
     * resolve the row for the <em>specific</em> task being added; matching on
     * taskDefinitionId narrows it to that task. Returns a List (not Optional) and is
     * ordered so the first element is deterministic — this stays crash-safe even if
     * the route legitimately repeats the same task at the same location.
     */
    List<PatrolDetail> findByPatrolIdAndLocationIdAndTaskDefinitionIdAndDeletedFalseOrderByDisplayOrderAscIdAsc(
            Long patrolId, Long locationId, Long taskDefinitionId);
    @Query(value = """
        SELECT
            pd.id                           AS patrolDetailId,
            td.name                         AS taskName,
            pd.task_definition_id           AS taskDefinitionId
        FROM patrol_detail pd
        LEFT JOIN task_definition td
            ON pd.task_definition_id = td.id
        LEFT JOIN patrol_task_distribution ptd
            ON ptd.patrol_detail_id = pd.id
            AND ptd.service_time_id = :serviceTimeId
        WHERE pd.patrol_id = :patrolId
          AND pd.location_id = :locationId
          AND td.customer_id = :customerId
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

    @Modifying
    @Query("UPDATE PatrolDetail pd SET pd.displayOrder = pd.displayOrder - 1 " +
           "WHERE pd.patrol.id = :patrolId AND pd.displayOrder > :oldPos AND pd.displayOrder <= :newPos")
    void shiftOrdersUp(@Param("patrolId") Long patrolId, @Param("oldPos") int oldPos, @Param("newPos") int newPos);

    @Modifying
    @Query("UPDATE PatrolDetail pd SET pd.displayOrder = pd.displayOrder + 1 " +
           "WHERE pd.patrol.id = :patrolId AND pd.displayOrder >= :newPos AND pd.displayOrder < :oldPos")
    void shiftOrdersDown(@Param("patrolId") Long patrolId, @Param("newPos") int newPos, @Param("oldPos") int oldPos);
}
