package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.DistributableTaskProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatrolDetailRepository extends JpaRepository<PatrolDetail,Long> {
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
}
