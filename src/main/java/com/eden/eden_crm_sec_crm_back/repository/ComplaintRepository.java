package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ComplaintEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<ComplaintEntity, Integer> {

    @Query("SELECT c FROM ComplaintEntity c WHERE c.deleted = false AND customer.id = :customerId")
    Page<ComplaintEntity> findByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    Optional<ComplaintEntity> findByIdAndCustomerId(Long id, Long customerId);

    @EntityGraph(attributePaths = {"customer", "customerSite", "contract"})
    @Query("""
            SELECT c FROM ComplaintEntity c
            WHERE c.deleted = false
            AND (:securityCompanyId IS NULL OR c.contract.securityCompanyId IN :securityCompanyId)
            AND (:customerId IS NULL OR c.customer.id IN :customerId)
            AND (:contractId IS NULL OR c.contract.id IN :contractId)
            AND (:operationSiteId IS NULL OR c.customerSite.id IN :operationSiteId)
            """)
    Page<ComplaintEntity> paginate(
            @Param("securityCompanyId") List<Long> securityCompanyId,
            @Param("customerId") List<Long> customerId,
            @Param("contractId") List<Long> contractId,
            @Param("operationSiteId") List<Long> operationSiteId,
            Pageable pageable
    );
}
