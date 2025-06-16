package com.eden.eden_crm_sec_crm_back.repository.lookup;

import com.eden.eden_crm_sec_crm_back.dto.external.ContractPlannedQntDto;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LKCustomerContractServiceRepository extends JpaRepository<LKCustomerContractService, Long> {
    @Query("""
            SELECT lkContService FROM LKCustomerContractService lkContService
            LEFT JOIN FETCH lkContService.customerService serviceDetails
            LEFT JOIN FETCH serviceDetails.customerService
            WHERE lkContService.customerContract.id = :contractId
            """)
    List<LKCustomerContractService> getContractServices(
            @Param("contractId") Long contractId
    );

    @Query("""
            SELECT lkContService FROM LKCustomerContractService lkContService
            LEFT JOIN FETCH lkContService.customerService serviceDetails
            LEFT JOIN FETCH serviceDetails.customerService
            WHERE lkContService.customerContract.id = :contractId
            AND lkContService.quantity > COALESCE(lkContService.distributedQuantity, 0)
            """)
    List<LKCustomerContractService> getContractNotFullyDistributedServices(
            @Param("contractId") Long contractId
    );

    @Query("""
            SELECT COALESCE(SUM(lk.distributedQuantity), 0)
            FROM LKCustomerContractService lk
            WHERE lk.customerContract.id = :contractId AND lk.id != :serviceId
            """)
    Long sumDistributedQnty(
            @Param("contractId") Long contractId,
            @Param("serviceId") Long serviceId
    );

    @Query("""
            SELECT COALESCE(SUM(lk.quantity), 0)
            FROM LKCustomerContractService lk
            WHERE lk.customerContract.id = :contractId AND lk.id != :serviceId
            """)
    Long sumQnty(
            @Param("contractId") Long contractId,
            @Param("serviceId") Long serviceId
    );

    @Query("""
            SELECT new com.eden.eden_crm_sec_crm_back.dto.external.ContractPlannedQntDto(
                c.id, c.agreementName, SUM(s.quantity)
            )
            FROM LKCustomerContractService s
            JOIN s.customerContract c
            WHERE c.customer.id = :customerId
              AND (:securityCompanyId IS NULL OR c.securityCompanyId = :securityCompanyId)
              AND (:contractIds IS NULL OR c.id IN :contractIds)
              AND (c.startAgreementDate <= :to AND c.endAgreementDate >= :from)
            GROUP BY c.id, c.agreementName
            """)
    List<ContractPlannedQntDto> sumPlannedQuantityByContract(
            @Param("customerId") Long customerId,
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("contractIds") List<Long> contractIds,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
            SELECT new com.eden.eden_crm_sec_crm_back.dto.external.ContractPlannedQntDto(
                c.id, c.agreementName, SUM(s.quantity)
            )
            FROM LKCustomerContractService s
            JOIN s.customerContract c
            WHERE c.customer.id = :customerId
              AND (:securityCompanyId IS NULL OR c.securityCompanyId = :securityCompanyId)
              AND (:contractIds IS NULL OR c.id IN :contractIds)
            GROUP BY c.id, c.agreementName
            """)
    List<ContractPlannedQntDto> sumPlannedQuantityByContract(
            @Param("customerId") Long customerId,
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("contractIds") List<Long> contractIds
    );

}
