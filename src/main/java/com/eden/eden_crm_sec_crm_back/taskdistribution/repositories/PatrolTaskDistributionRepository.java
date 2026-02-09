package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolReportDetailsAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface PatrolTaskDistributionRepository extends JpaRepository<PatrolTaskDistribution, Long>, JpaSpecificationExecutor<PatrolTaskDistribution> {
    boolean existsByServiceTime_IdAndPatrolDetail_Id(Long serviceTimeId, Long patrolDetailId);
    boolean existsByIdAndLocation_IdAndTaskDistribution_Task_Id(Long id, Long locationId, Long taskId);

    // Aggregate patrols by premise and patrol using TaskExecutionSlot for timing/status
    @Query("""
        SELECT
            COALESCE(l.premise.id, pd.location.premise.id) AS premiseId,
            pd.patrol.id AS patrolId,
            pd.patrol.name AS patrolName,
            MIN(FUNCTION('DATE', tes.startDateTime)) AS patrolStartDate,
            pd.patrol.frequency AS patrolFrequencyType,
            COUNT(ptd.id) AS assignedCount,
            SUM(CASE WHEN tes.status = 'FINISHED' THEN 1 ELSE 0 END) AS finishedCount
        FROM PatrolTaskDistribution ptd
        LEFT JOIN ptd.patrolDetail pd
        LEFT JOIN pd.patrol
        LEFT JOIN ptd.location l
        LEFT JOIN TaskExecutionSlot tes ON tes.taskDistribution = ptd.taskDistribution
        WHERE ptd.taskDistribution.contract.id = :contractId
          AND (:premiseIds IS NULL OR COALESCE(l.premise.id, pd.location.premise.id) IN :premiseIds)
          AND (:patrolIds IS NULL OR pd.patrol.id IN :patrolIds)
          AND (:locationIds IS NULL OR l.id IN :locationIds)
          AND (:startDate IS NULL OR FUNCTION('DATE', tes.startDateTime) >= :startDate)
          AND (:endDate IS NULL OR FUNCTION('DATE', tes.startDateTime) <= :endDate)
        GROUP BY COALESCE(l.premise.id, pd.location.premise.id), pd.patrol.id, pd.patrol.name, pd.patrol.frequency
    """)
    List<PatrolPremiseAggregation> aggregatePatrolsByPremiseAndPatrol(
            @Param("contractId") Long contractId,
            @Param("premiseIds") Set<Long> premiseId,
            @Param("patrolIds") Set<Long> patrolIds,
            @Param("locationIds") Set<Long> locationIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Find patrol report details (tasks grouped by location/service/task) using TaskExecutionSlot + TaskCheck patrol execution
    @Query("""
        SELECT
            COALESCE(l.id, pd.location.id) AS locationId,
            NULL AS siteId,
            ptd.service.id AS serviceId,
            pd.task.id AS taskId,
            COALESCE(l.name, pd.location.name) AS siteName,
            ptd.service.customerService.customerService.serviceName AS serviceName,
            pd.task.name AS taskName,
            tes.status AS status,
            tc.evidence AS hasEvidence,
            te.image AS evidenceImage,
            te.comment AS comment,
            te.commentCheck AS commentCheck,
            MIN(FUNCTION('DATE', tes.startDateTime)) AS taskStartDate,
            MAX(FUNCTION('DATE', tes.endDateTime)) AS taskEndDate
        FROM PatrolTaskDistribution ptd
        JOIN ptd.patrolDetail pd
        LEFT JOIN ptd.location l
        LEFT JOIN pd.task t
        LEFT JOIN t.taskChecks tc
        LEFT JOIN TaskCheckPatrolExecution te ON te.id = tc.id
        LEFT JOIN TaskExecutionSlot tes ON tes.taskDistribution = ptd.taskDistribution
        WHERE pd.location.premise.id = :premiseId
          AND pd.patrol.id = :patrolId
        GROUP BY COALESCE(l.id, pd.location.id), ptd.service.id, pd.task.id, COALESCE(l.name, pd.location.name), ptd.service.serviceName, pd.task.name, tes.status, tc.evidence, te.image, te.comment, te.commentCheck
    """)
    List<PatrolReportDetailsAggregation> findPatrolDetails(@Param("premiseId") Long premiseId, @Param("patrolId") Long patrolId);

    // Keep basic finders for other uses
    List<PatrolTaskDistribution> findByPatrolDetail_Id(Long patrolDetailId);
    List<PatrolTaskDistribution> findByServiceTime_Id(Long serviceTimeId);
}
