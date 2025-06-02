package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SiteDistributionRepository extends JpaRepository<SiteDistribution, Long> {
    @Query("""
            SELECT sd FROM SiteDistribution sd
            WHERE sd.customerContract.id = :contractId
            AND sd.lkCustomerContractService.id = :lkCustomerServiceId
            """)
    List<SiteDistribution> findByContractAndLKCustomerService(Long contractId, Long lkCustomerServiceId);

    @Query("""
            SELECT sd FROM SiteDistribution sd
            LEFT JOIN FETCH sd.site
            WHERE sd.site.id = :siteId
            AND sd.customerContract.startAgreementDate <= :today
            AND sd.customerContract.endAgreementDate >= :today
            """)
    List<SiteDistribution> findByActiveTodayAndSiteId(
            @Param("siteId") Long siteId,
            @Param("today") LocalDate today
    );

}