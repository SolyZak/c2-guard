package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerContractRepository extends BaseRepository<CustomerContract, Long> {
    @EntityGraph(attributePaths = {"operationRule", "services", "siteDistributions"})
    Optional<CustomerContract> findWithDetailsById(Long contractId);
}
