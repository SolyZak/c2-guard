package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatrolTaskDistributionRepository extends JpaRepository<PatrolTaskDistribution, Long>, JpaSpecificationExecutor<PatrolTaskDistribution> {
    boolean existsByServiceTime_IdAndPatrolDetail_Id(Long serviceTimeId, Long patrolDetailId);
    boolean existsByIdAndLocation_IdAndTaskDistribution_TaskDefinitionId(Long id, Long locationId, Long taskDefinitionId);

    /**
     * All rows belonging to a single (service, patrol, serviceTime, site)
     * assignment, regardless of location. Used by the US2 GET endpoint to
     * project the per-location task grid and by the PATCH endpoint to resolve
     * which rows to mutate.
     */
    @Query("""
            SELECT p FROM PatrolTaskDistribution p
              JOIN FETCH p.taskDistribution td
              JOIN FETCH p.location loc
              JOIN FETCH p.patrolDetail pd
            WHERE p.service.id     = :serviceId
              AND p.serviceTime.id = :serviceTimeId
              AND pd.patrol.id     = :patrolId
              AND p.serviceTime.siteDistribution.site.id = :siteId
            """)
    List<PatrolTaskDistribution> findAssignmentRows(
            @Param("serviceId") Long serviceId,
            @Param("patrolId") Long patrolId,
            @Param("serviceTimeId") Long serviceTimeId,
            @Param("siteId") Long siteId
    );

    /** All rows for a given (patrol, service, site) across ALL service times. Used by US1 patrol edit. */
    @Query("""
            SELECT p FROM PatrolTaskDistribution p
              JOIN FETCH p.taskDistribution td
              JOIN FETCH p.patrolDetail pd
            WHERE pd.patrol.id = :patrolId
              AND p.service.id = :serviceId
              AND p.serviceTime.siteDistribution.site.id = :siteId
            """)
    List<PatrolTaskDistribution> findByPatrolAndServiceAndSite(
            @Param("patrolId") Long patrolId,
            @Param("serviceId") Long serviceId,
            @Param("siteId") Long siteId
    );

    /** Locate the specific row(s) matching a (location, taskDefinitionId) inside an assignment. */
    @Query("""
            SELECT p FROM PatrolTaskDistribution p
            WHERE p.service.id     = :serviceId
              AND p.serviceTime.id = :serviceTimeId
              AND p.location.id    = :locationId
              AND p.patrolDetail.patrol.id = :patrolId
              AND p.taskDistribution.taskDefinitionId = :taskDefinitionId
              AND p.serviceTime.siteDistribution.site.id = :siteId
            """)
    List<PatrolTaskDistribution> findInAssignmentByLocationAndTaskDef(
            @Param("serviceId") Long serviceId,
            @Param("patrolId") Long patrolId,
            @Param("serviceTimeId") Long serviceTimeId,
            @Param("siteId") Long siteId,
            @Param("locationId") Long locationId,
            @Param("taskDefinitionId") Long taskDefinitionId
    );
}
