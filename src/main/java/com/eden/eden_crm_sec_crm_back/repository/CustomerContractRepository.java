package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.projections.GeneralDropdownProjection;
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

    @Query("SELECT DISTINCT cc.customer FROM CustomerContract cc " +
            "WHERE cc.securityCompanyId = :securityCompanyId " +
            "AND cc.startAgreementDate <= :today " +
            "AND cc.endAgreementDate >= :today")
    List<Customer> listCustomerBySecurityCompanyIdAndActiveToday(
            @NotNull @Param("securityCompanyId") Long securityCompanyId,
            @NotNull @Param("today") LocalDate today
    );

    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
            "WHERE cc.securityCompanyId = :securityCompanyId " +
            "AND cc.startAgreementDate <= :today " +
            "AND cc.endAgreementDate >= :today " +
            "AND cc.customer.id = :customerId")
    List<CustomerContract> listByCustomerIdSecurityCompanyIdAndActiveToday(
            @NotNull @Param("securityCompanyId") Long securityCompanyId,
            @NotNull @Param("customerId") Long customerId,
            @NotNull @Param("today") LocalDate today
    );

    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
            "WHERE cc.securityCompanyId = :securityCompanyId " +
            "AND cc.startAgreementDate <= :today " +
            "AND cc.endAgreementDate >= :today " +
            "AND cc.customer.id = :customerId " +
            "AND cc.id = :contractId")
    @EntityGraph(attributePaths = {"customerAgreement", "siteDistributions"})
    Optional<CustomerContract> findByCustomerIdSecurityCompanyIdAndActiveToday(
            @NotNull @Param("securityCompanyId") Long securityCompanyId,
            @NotNull @Param("customerId") Long customerId,
            @NotNull @Param("contractId") Long contractId,
            @NotNull @Param("today") LocalDate today
    );

    @Query("""
            SELECT cc.securityCompanyId AS id, cc.securityCompanyName AS name
            FROM CustomerContract cc
            WHERE cc.customer.id = :customerId
            GROUP BY cc.securityCompanyId, cc.securityCompanyName
            """)
    List<GeneralDropdownProjection> securityCompanies(@Param("customerId") Long customerId);

    @Query("""
            SELECT cc.id AS id, cc.agreementName AS name
            FROM CustomerContract cc
            WHERE cc.customer.id = :customerId
            AND (:securityCompanyId IS NULL OR cc.securityCompanyId = :securityCompanyId)
            """)
    List<GeneralDropdownProjection> contractsDropdown(
            @Param("customerId") Long customerId,
            @Param("securityCompanyId") Long securityCompanyId
    );

    @Query("SELECT c FROM CustomerContract c " +
            "WHERE (:securityCompanyId IS NULL OR c.securityCompanyId = :securityCompanyId) " +
            "AND (:contractIds IS NULL OR c.id IN :contractIds) " +
            "AND (c.startAgreementDate <= :to AND c.endAgreementDate >= :from)" +
            "AND c.customer.id = :customerId "
    )
    @EntityGraph(attributePaths = {"customerAgreement", "siteDistributions.operationServices"})
    List<CustomerContract> listContracts(
            @Param("customerId") Long customerId,
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("contractIds") List<Long> contractIds,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT c FROM CustomerContract c " +
            "WHERE (:securityCompanyId IS NULL OR c.securityCompanyId = :securityCompanyId) " +
            "AND (:contractIds IS NULL OR c.id IN :contractIds) " +
            "AND c.customer.id = :customerId "
    )
    @EntityGraph(attributePaths = {"customerAgreement", "siteDistributions.operationServices"})
    List<CustomerContract> listContracts(
            @Param("customerId") Long customerId,
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("contractIds") List<Long> contractIds
    );
}
