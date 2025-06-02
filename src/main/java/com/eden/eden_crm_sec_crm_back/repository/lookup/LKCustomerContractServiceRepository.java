package com.eden.eden_crm_sec_crm_back.repository.lookup;

import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
            AND lkContService.quantity > lkContService.distributedQuantity
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
}
