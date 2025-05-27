package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerContractRepository extends BaseRepository<CustomerContract, Long> {
    @EntityGraph(attributePaths = {"operationRule", "services", "siteDistributions"})
    Optional<CustomerContract> findWithDetailsById(Long contractId);

    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
            "LEFT JOIN FETCH cc.customerContractServices ccs " +
            "LEFT JOIN FETCH ccs.customerService " +
            "WHERE cc.agreementNumber = :agreementNumber")
    Optional<CustomerContract> findByAgreementNumberWithServices(@Param("agreementNumber") String agreementNumber);
//    @Query("SELECT DISTINCT cc FROM CustomerContract cc " +
//            "LEFT JOIN FETCH cc.customerContractServices ccs " +
//            "LEFT JOIN FETCH ccs.customerService cs " +
//            "LEFT JOIN FETCH cs.serviceDetails " +
//            "WHERE cc.agreementNumber = :agreementNumber")
//    Optional<CustomerContract> findByAgreementNumberWithServices(@Param("agreementNumber") String agreementNumber);

}
