package com.eden.eden_crm_sec_crm_back.patrols.repositories;

import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolReportDetailsAggregation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface PatrolRepository extends JpaRepository<Patrol, Long> {
    @Query(value = """
                    select distinct p.*
                    from patrol p
                    where p.customer_id = :customerId
                    and (
                      :search is null
                      or lower(p.name) like lower(concat('%', :search, '%'))
                      or exists (
                        select 1
                        from location l
                        join patrol_detail lpd on l.id = lpd.location_id
                        where lpd.patrol_id = p.id
                        and lower(l.name) like lower(concat('%', :search, '%'))
                      )
                      or exists (
                        select 1
                        from task t
                        join patrol_detail tpd on t.id = tpd.task_id
                        where tpd.patrol_id = p.id
                        and lower(t.name) like lower(concat('%', :search, '%'))
                      )
                    )
            """, nativeQuery = true)
    Page<Patrol> patrolPaginate(Pageable pageable,
                                          @Param("search") String search,
                                          @Param("customerId") Long customerId);

    @Query("""
            select p from Patrol p where p.customer.id = :customerId
            """)
    List<Patrol> listAllLoggedInCustomerPatrols(@Param("customerId") Long customerId);

    @Query("""
        SELECT
            ptd.location.premise.id AS premiseId,
            ptd.location.premise.name AS premiseName,
            ptd.location.premise.code AS premiseCode,
            pd.patrol.id AS patrolId,
            pd.patrol.name AS patrolName,
            pd.patrol.frequency AS patrolFrequencyType,
            MIN(tes.startDateTime) AS patrolStartDateTime,
            COUNT(ptd.id) AS assignedCount,
            SUM(CASE WHEN tes.status = 'FINISHED' THEN 1 ELSE 0 END) AS finishedCount
        FROM PatrolTaskDistribution ptd
        JOIN ptd.patrolDetail pd
        JOIN TaskExecutionSlot tes ON tes.taskDistribution = ptd.taskDistribution
        WHERE ptd.taskDistribution.contract.id = :contractId
          AND tes.startDateTime BETWEEN :startDateTime AND :endDateTime
          AND (:premiseIds IS NULL OR ptd.location.premise.id IN :premiseIds)
          AND (:patrolIds IS NULL OR pd.patrol.id IN :patrolIds)
          AND (:locationIds IS NULL OR ptd.location.id IN :locationIds)
        GROUP BY premiseId, premiseName, premiseCode, patrolId, patrolName, patrolFrequencyType
    """)
    List<PatrolPremiseAggregation> aggregatePatrolsByPremiseAndPatrol(
            @Param("contractId") Long contractId,
            @Param("startDateTime") OffsetDateTime startDateTime,
            @Param("endDateTime") OffsetDateTime endDateTime,
            @Param("premiseIds") Set<Long> premiseId,
            @Param("patrolIds") Set<Long> patrolIds,
            @Param("locationIds") Set<Long> locationIds
    );

    @Query("""
        SELECT
            ptd.location.id AS locationId,
            ptd.location.name AS locationName,
            ptd.serviceTime.siteDistribution.site.id AS siteId,
            ptd.serviceTime.siteDistribution.site.name AS siteName,
            ptd.service.id AS serviceId,
            ptd.service.customerService.customerService.serviceName AS serviceName,
            pd.task.id AS taskId,
            pd.task.name AS taskName,
            tes.status AS status,
            tc.evidence AS hasEvidence,
            te.image AS evidenceImage,
            te.commentCheck AS commentCheck,
            te.comment AS comment,
            MIN(tes.startDateTime) AS taskStartDateTime,
            MAX(tes.endDateTime) AS taskEndDateTime
        FROM PatrolTaskDistribution ptd
        JOIN ptd.patrolDetail pd
        LEFT JOIN pd.task.taskChecks tc
        LEFT JOIN TaskCheckPatrolExecution te ON te.id = tc.id
        LEFT JOIN TaskExecutionSlot tes ON tes.taskDistribution = ptd.taskDistribution
        WHERE ptd.location.premise.id = :premiseId
          AND pd.patrol.id = :patrolId
        GROUP BY locationId, locationName, siteId, siteName, serviceId, serviceName, taskId, taskName, status, hasEvidence, evidenceImage, commentCheck, comment
    """)
    List<PatrolReportDetailsAggregation> findPatrolDetails(@Param("premiseId") Long premiseId, @Param("patrolId") Long patrolId);
}
