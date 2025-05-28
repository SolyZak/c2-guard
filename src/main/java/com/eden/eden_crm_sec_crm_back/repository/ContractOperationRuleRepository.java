package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ContractOperationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractOperationRuleRepository extends JpaRepository<ContractOperationRule, Long> {
    ContractOperationRule findByCustomerAgreementId(Long customerAgreementId);

    @Query("SELECT DISTINCT cr FROM ContractOperationRule cr " +
            "WHERE cr.customerAgreement.id = :contractId")
    Optional<ContractOperationRule> findContractRule(Long contractId);
}
