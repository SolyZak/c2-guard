package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
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
}
