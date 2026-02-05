package com.eden.eden_crm_sec_crm_back.repository.lookup;

import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.projections.DistributionTimesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("""
                SELECT s.id as id, s.fromTime as startTime, s.toTime as endTime FROM LKCustomerContractOperationService s
                WHERE s.siteDistribution.id = :distributionId
            """)
    List<DistributionTimesProjection> findAllOffsetStartAndEndByDistributionId(
            @Param("distributionId") Long distributionId
    );

    Optional<LKCustomerContractOperationService> findByIdAndSiteDistribution_Id(Long id, Long siteDistributionId);

    /* Include the service only when there exists at least one PatrolDetail for the given
       patrol that is NOT yet paired (via PatrolTaskDistribution) with this service.
     */
    @Query("""
        SELECT DISTINCT s FROM LKCustomerContractOperationService s
        JOIN s.siteDistribution sd
        WHERE sd.customerContract.id = :contractId
        AND sd.lkCustomerContractService.id = :serviceId
        AND sd.site.id = :siteId
        AND EXISTS (
            SELECT pd FROM PatrolDetail pd
            WHERE pd.patrol.id = :patrolId
              AND NOT EXISTS (
                  SELECT ptd FROM PatrolTaskDistribution ptd
                  WHERE ptd.serviceTime = s
                    AND ptd.patrolDetail = pd
              )
        )
    """)
    List<LKCustomerContractOperationService> findAvailableServiceTimes(
        @Param("contractId") Long contractId,
        @Param("serviceId") Long serviceId,
        @Param("siteId") Long siteId,
        @Param("patrolId") Long patrolId
    );
}
