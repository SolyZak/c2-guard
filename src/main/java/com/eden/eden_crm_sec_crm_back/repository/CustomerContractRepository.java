package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerContractRepository extends BaseRepository<CustomerContract, Long> {
}
