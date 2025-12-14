package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractOperationSiteDistributionPatrolRepository extends JpaRepository<ContractOperationSiteDistributionPatrol, Long>, JpaSpecificationExecutor<ContractOperationSiteDistributionPatrol> {

    @Query("SELECT p FROM ContractOperationSiteDistributionPatrol p WHERE p.customerContract.id = :contractId")
    List<ContractOperationSiteDistributionPatrol> findAllByCustomerContractId(@Param("contractId") Long contractId);

    @Query("""
        SELECT 
            CASE 
                WHEN s.premise.id IS NOT NULL THEN s.premise.id 
                WHEN l.premise.id IS NOT NULL THEN l.premise.id
            END as premiseId,
            p.patrol.id as patrolId,
            p.patrol.name as patrolName,
            MIN(p.startDate) as patrolStartDate,
            p.patrolFrequencyType as patrolFrequencyType,
            COUNT(p.id) as assignedCount,
            SUM(CASE WHEN p.status = 'FINISHED' THEN 1 ELSE 0 END) as finishedCount
        FROM ContractOperationSiteDistributionPatrol p
        LEFT JOIN p.location l
        LEFT JOIN p.site s
        WHERE p.customerContract.id = :contractId
        GROUP BY premiseId, p.patrol.id, p.patrol.name, p.patrolFrequencyType
    """)
    List<PatrolPremiseAggregation> aggregatePatrolsByPremiseAndPatrol(@Param("contractId") Long contractId);
}
