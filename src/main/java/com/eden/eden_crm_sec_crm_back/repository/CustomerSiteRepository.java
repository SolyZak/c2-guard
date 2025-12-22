package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.projections.GeneralDropdownProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    //this was used before
    @Query("""
            SELECT cs.id as id, cs.name as name FROM CustomerSite cs
            WHERE cs.customer.id = :customerId
            """)
    List<GeneralDropdownProjection> operationSitesDropdown(@Param("customerId") Long customerId);

    @Query("""
                SELECT cs.id as id, cs.name as name FROM CustomerSite cs
                WHERE EXISTS (
                    SELECT sd FROM SiteDistribution sd
                    WHERE sd.site = cs
                    AND sd.customerContract.securityCompanyId = :securityCompanyId
                    AND sd.customerContract.startAgreementDate <= :today
                    AND sd.customerContract.endAgreementDate >= :today
                )
            """)
    List<GeneralDropdownProjection> findBySecurityCompanyId(
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("today") LocalDate today
    );

    @Query("""
                SELECT cs FROM CustomerSite cs JOIN Premise p on p.id = cs.premise.id where cs.customer.id = :customerId AND (lower(p.name) like lower(concat('%', :search, '%')) OR lower(cs.name) like lower(concat('%',:search,'%')) OR :search is null)
            """)
    Page<CustomerSite> searchByCustomerSiteNameAndPremiseName(
            @Param("search") String search, @Param("customerId") Long customerId,
            Pageable pageable
    );
    @Query("""
       select cs.id                                             as id,
              concat(cs.name, ' - ', coalesce(p.name, ''))      as name
       from   CustomerSite cs
       left  join cs.premise p
       where  cs.customer.id = :customerId
       """)
    List<GeneralDropdownProjection> findCustomerSitesForDropdown(@Param("customerId") Long customerId);


}
