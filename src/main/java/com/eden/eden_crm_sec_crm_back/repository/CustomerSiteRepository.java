package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.projections.GeneralDropdownProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomerSiteRepository extends JpaRepository<CustomerSite, Long>, JpaSpecificationExecutor<CustomerSite> {

    List<CustomerSite> findByCustomerId(Long customerId);

    Optional<CustomerSite> findByIdAndCustomerId(Long id, Long customerId);

    @Query("""
            SELECT cs FROM CustomerSite cs
            WHERE cs.customer.id = :customerId
            AND cs.id IN :ids
            """)
    List<CustomerSite> listByIdAndCustomerId(@Param("ids") List<Long> ids, @Param("customerId") Long customerId);

    @Query("""
            SELECT DISTINCT cs FROM CustomerSite cs
            WHERE cs.customer.id = :customerId AND (
                EXISTS (
                    SELECT sd FROM SiteDistribution sd
                    WHERE sd.site = cs AND sd.customerContract.id = :contractId
                )
                OR NOT EXISTS (
                    SELECT sd FROM SiteDistribution sd
                    WHERE sd.site = cs AND (
                        sd.customerContract.startAgreementDate <= :endDate AND
                        sd.customerContract.endAgreementDate >= :startDate AND
                        sd.customerContract.id <> :contractId
                    )
                )
            )
            """)
    List<CustomerSite> findAvailableSitesForContract(
            @Param("customerId") Long customerId,
            @Param("contractId") Long contractId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT cs.id as id, cs.name as name FROM CustomerSite cs
            WHERE cs.customer.id = :customerId
            """)
    List<GeneralDropdownProjection> operationSitesDropdown(@Param("customerId") Long customerId);

    @Query("""
                SELECT cs.id as id, cs.name as name FROM CustomerSite cs
                WHERE EXISTS (
                    SELECT sd FROM SiteDistribution sd
                    WHERE sd.site = cs AND sd.customerContract.securityCompanyId = :securityCompanyId
                    AND (
                        sd.customerContract.startAgreementDate >= :today AND
                        sd.customerContract.endAgreementDate >= :today AND
                    )
                )
            """)
    List<GeneralDropdownProjection> findBySecurityCompanyId(
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("today") LocalDate today
    );
}
