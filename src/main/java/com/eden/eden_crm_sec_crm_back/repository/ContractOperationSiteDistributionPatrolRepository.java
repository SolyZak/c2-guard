package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractOperationSiteDistributionPatrolRepository extends JpaRepository<ContractOperationSiteDistributionPatrol, Long> {

    @Query("SELECT p FROM ContractOperationSiteDistributionPatrol p WHERE p.customerContract.id = :contractId")
    List<ContractOperationSiteDistributionPatrol> findAllByCustomerContractId(@Param("contractId") Long contractId);
}
