package com.eden.eden_crm_sec_crm_back.repository.lookup;

import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LKCustomerContractOperationServiceRepository extends JpaRepository<LKCustomerContractOperationService, Long> {
    @Query("""
                SELECT s FROM LKCustomerContractOperationService s
                WHERE s.siteDistribution.customerContract.customer.id = :customerId
                AND s.siteDistribution.site.id = :siteId
                AND s.siteDistribution.customerContract.id = :contractId
            """)
    List<LKCustomerContractOperationService> findContractOperationServices(
            @Param("customerId") Long customerId,
            @Param("siteId") Long siteId,
            @Param("contractId") Long contractId
    );

    @Query("""
                SELECT s FROM LKCustomerContractOperationService s
                WHERE s.siteDistribution.customerContract.securityCompanyId = :securityCompanyId
                AND s.siteDistribution.site.id = :siteId
                AND s.siteDistribution.customerContract.id = :contractId
            """)
    List<LKCustomerContractOperationService> findSecurityContractOperationServices(
            @Param("securityCompanyId") Long securityCompanyId,
            @Param("siteId") Long siteId,
            @Param("contractId") Long contractId
    );
}
