package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
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
public interface ContractOperationSiteDistributionPatrolRepository extends JpaRepository<ContractOperationSiteDistributionPatrol, Long>, JpaSpecificationExecutor<ContractOperationSiteDistributionPatrol> {

    @Query("SELECT p FROM ContractOperationSiteDistributionPatrol p WHERE p.customerContract.id = :contractId")
    List<ContractOperationSiteDistributionPatrol> findAllByCustomerContractId(@Param("contractId") Long contractId);

    @Query("""    
         SELECT
            COALESCE(s.premise.id, l.premise.id) AS premiseId,
            p.patrol.id AS patrolId,
            p.patrol.name AS patrolName,
            MIN(p.startDate) AS patrolStartDate,
            p.patrolFrequencyType AS patrolFrequencyType,
            COUNT(p.id) AS assignedCount,
            SUM(CASE WHEN p.status = 'FINISHED' THEN 1 ELSE 0 END) AS finishedCount
         FROM ContractOperationSiteDistributionPatrol p
         LEFT JOIN p.location l
         LEFT JOIN p.site s
         WHERE p.customerContract.id = :contractId
           AND (:premiseIds IS NULL OR COALESCE(s.premise.id, l.premise.id) IN :premiseIds)
           AND (:patrolIds IS NULL OR p.patrol.id IN :patrolIds)
           AND (:locationIds IS NULL OR l.id IN :locationIds)
           AND p.startDate >= COALESCE(:startDate, p.startDate)
           AND p.startDate <= COALESCE(:endDate,   p.startDate)
         GROUP BY COALESCE(s.premise.id, l.premise.id), p.patrol.id, p.patrol.name, p.patrolFrequencyType
    """)
    List<PatrolPremiseAggregation> aggregatePatrolsByPremiseAndPatrol(
            @Param("contractId") Long contractId,
            @Param("premiseIds") Set<Long> premiseId,
            @Param("patrolIds") Set<Long> patrolIds,
            @Param("locationIds") Set<Long> locationIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT 
            l.id AS locationId, 
            s.id AS siteId,
            cs.id AS serviceId,
            t.id AS taskId, 
            s.name AS siteName,
            cs.serviceName AS serviceName,
            t.name AS taskName, 
            cosdp.status AS status,
            tc.evidence AS hasEvidence, 
            te.image AS evidenceImage, 
            te.comment AS comment,
            te.commentCheck AS commentCheck,
            MIN(cosdp.startDate) AS taskStartDate, 
            MAX(cosdp.endDate) AS taskEndDate
        FROM ContractOperationSiteDistributionPatrol cosdp
        JOIN cosdp.patrol patrol
        JOIN cosdp.site s
        JOIN cosdp.location l
        JOIN cosdp.location.premise p
        JOIN cosdp.customerService.customerService.customerService cs
        JOIN cosdp.task t
        LEFT JOIN t.taskChecks tc
        LEFT JOIN TaskCheckPatrolExecution te ON te.id = tc.id
        WHERE p.id = :premiseId
          AND patrol.id = :patrolId
        GROUP BY locationId, siteId, serviceId, taskId, serviceName, siteName, taskName, status, hasEvidence, evidenceImage, te.comment, te.commentCheck
    """)
    List<PatrolReportDetailsAggregation> findPatrolDetails(@Param("premiseId") Long premiseId, @Param("patrolId") Long patrolId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1 
            FROM contract_operation_distribution_site_patrol 
            WHERE id = :id 
              AND task_id = :taskId 
              AND location_id = :locationId
        )
    """, nativeQuery = true)
    boolean existsByIdAndTaskIdAndLocationId(
            @Param("id") Long id,
            @Param("taskId") Long taskId,
            @Param("locationId") Long locationId
    );
}
