package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteDistributionRepository extends JpaRepository<SiteDistribution, Long> {
    @Query("""
            SELECT sd FROM SiteDistribution sd
            WHERE sd.customerContract.id = :contractId
            AND sd.lkCustomerContractService.id = :lkCustomerServiceId
            """)
    List<SiteDistribution> findByContractAndLKCustomerService(Long contractId, Long lkCustomerServiceId);
}
