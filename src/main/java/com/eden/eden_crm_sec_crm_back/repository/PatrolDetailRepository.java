package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.DistributableTaskProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatrolDetailRepository extends JpaRepository<PatrolDetail,Long> {
    @Query("""
        SELECT
            pd.id as patrolDetailId,
            pd.task.name as taskName
        FROM PatrolDetail pd
        LEFT JOIN PatrolTaskDistribution ptd
            WITH ptd.patrolDetail = pd AND ptd.serviceTime.id = :serviceTimeId
        WHERE pd.patrol.id = :patrolId
          AND pd.location.id = :locationId
          AND pd.task.customer.id = :customerId
          AND ptd.id IS NULL
    """)
    List<DistributableTaskProjection> getDistributableTasks(
        @Param("customerId") Long customerId,
        @Param("patrolId") Long patrolId,
        @Param("locationId") Long locationId,
        @Param("serviceTimeId") Long serviceTimeId
    );

    long countByPatrol_Id(Long patrolId);
}
