package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerContractRepository extends JpaRepository<CustomerContract, Long> {
    @EntityGraph(attributePaths = {"customerAgreement", "customerContractServices", "siteDistributions"})
    Optional<CustomerContract> findWithDetailsByIdAndCustomerId(Long contractId, Long customerId);

    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
            "WHERE cc.id = :id " +
            "AND cc.customer.id = :customerId")
    Optional<CustomerContract> findByIdAndCustomerId(
            @Param("id") Long id,
            @Param("customerId") Long customerId
    );

    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
            "WHERE cc.status IN :statuses " +
            "AND cc.customer.id = :customerId")
    List<CustomerContract> listByCustomerIdAndStatus(
            @NotNull @Param("customerId") Long customerId,
            @NotEmpty @Param("statuses") List<ContractStatus> statuses
    );

    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
            "WHERE cc.customer.id = :customerId")
    List<CustomerContract> listByCustomerId(
            @NotNull @Param("customerId") Long customerId
    );

    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
            "LEFT JOIN FETCH cc.customerAgreement " +
            "WHERE cc.customer.id = :customerId")
    List<CustomerContract> listByCustomerIdWithRules(
            @NotNull @Param("customerId") Long customerId
    );

    @Query(value = """
        SELECT * FROM customer_contract cc
        WHERE cc.customer_id = :customerId
        AND (
            :search IS NULL OR
            cc.agreement_number ILIKE CONCAT('%', :search, '%') OR
            cc.agreement_name ILIKE CONCAT('%', :search, '%')
        )
        AND (
            (:from IS NULL AND :to IS NULL) OR
            (cc.start_agreement_date <= COALESCE(:to, cc.start_agreement_date) 
             AND cc.end_agreement_date >= COALESCE(:from, cc.end_agreement_date))
        )
        """,
        countQuery = """
        SELECT COUNT(*) FROM customer_contract cc
        WHERE cc.customer_id = :customerId
        AND (
            :search IS NULL OR
            cc.agreement_number ILIKE CONCAT('%', :search, '%') OR
            cc.agreement_name ILIKE CONCAT('%', :search, '%')
        )
        AND (
            (:from IS NULL AND :to IS NULL) OR
            (cc.start_agreement_date <= COALESCE(:to, cc.start_agreement_date) 
             AND cc.end_agreement_date >= COALESCE(:from, cc.end_agreement_date))
        )
        """,
        nativeQuery = true
    )
    Page<CustomerContract> paginateByCustomer(
            @Param("customerId") Long customerId,
            @Param("search") String search,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable
    );
}
