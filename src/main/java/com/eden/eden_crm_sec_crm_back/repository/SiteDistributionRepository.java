package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.projections.GeneralDropdownProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
            WHERE sd.customerContract.id = :contractId
            AND sd.lkCustomerContractService.id = :lkCustomerServiceId
            """)
    Optional<SiteDistribution> findOneByContractAndLKCustomerService(Long contractId, Long lkCustomerServiceId);

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

    @Query("""
            SELECT sd FROM SiteDistribution sd
            LEFT JOIN FETCH sd.site
            LEFT JOIN FETCH sd.customerContract
            WHERE sd.site.id = :siteId
            AND sd.customerContract.startAgreementDate <= :today
            AND sd.customerContract.endAgreementDate >= :today
            AND sd.customerContract.securityCompanyId = :securityCompanyId
            """)
    List<SiteDistribution> listForSecurityCompanyActiveTodayAndSiteId(
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("siteId") Long siteId,
            @Param("today") LocalDate today
    );

    boolean existsBySiteId(Long siteId);

    @EntityGraph(attributePaths = {
            "site",
            "customerContract",
            "lkCustomerContractService",
            "operationServices"
    })
    @Query("""
            SELECT sd FROM SiteDistribution sd
            WHERE (sd.customerContract.startAgreementDate <= :to AND sd.customerContract.endAgreementDate >= :from)
            AND (:securityCompanyId IS NULL OR sd.customerContract.securityCompanyId IN :securityCompanyId)
            AND (:customerId IS NULL OR sd.customerContract.customer.id IN :customerId)
            AND (:contractId IS NULL OR sd.customerContract.id IN :contractId)
            AND (:operationSiteId IS NULL OR sd.site.id IN :operationSiteId)
            """)
    List<SiteDistribution> listSiteDistributionForStats(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("securityCompanyId") List<Long> securityCompanyId,
            @Param("customerId") List<Long> customerId,
            @Param("contractId") List<Long> contractId,
            @Param("operationSiteId") List<Long> operationSiteId
    );

    @Query("""
            SELECT sd.site.id AS id, sd.site.name AS name
            FROM SiteDistribution sd
            WHERE sd.site.customer.id = :customerId
              AND (:securityCompanyId IS NULL OR sd.customerContract.securityCompanyId = :securityCompanyId)
              AND (:contractId IS NULL OR sd.customerContract.id IN :contractId)
            GROUP BY sd.site.id, sd.site.name
            """)
    List<GeneralDropdownProjection> operationSitesDropdown(
            @Param("customerId") Long customerId,
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("contractId") List<Long> contractId
    );

    @Query("""
            SELECT sd.site.id AS id, sd.site.name AS name
            FROM SiteDistribution sd
            WHERE sd.customerContract.securityCompanyId = :securityCompanyId
              AND (:customerId IS NULL OR  sd.site.customer.id IN :customerId)
              AND (:contractId IS NULL OR sd.customerContract.id IN :contractId)
            GROUP BY sd.site.id, sd.site.name
            """)
    List<GeneralDropdownProjection> operationSitesDropdown(
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("customerId") List<Long> customerId,
            @Param("contractId") List<Long> contractId
    );

    @Query("""
                SELECT sd FROM SiteDistribution sd
                LEFT JOIN FETCH sd.lkCustomerContractService
                LEFT JOIN FETCH sd.operationServices
                LEFT JOIN FETCH sd.site
                WHERE sd.customerContract.id = :contractId
            """)
    List<SiteDistribution> findDistributionsByContractId(@Param("contractId") Long contractId);

    @Query("""
            SELECT DISTINCT sd.site FROM SiteDistribution sd
            WHERE sd.site.id = :id
            AND sd.customerContract.id = :contractId
            """)
    Optional<CustomerSite> findByIdAndContractId(
            @Param("id") Long id,
            @Param("contractId") Long contractId
    );

}