package com.eden.eden_crm_sec_crm_back.service.impl.lookup;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.lookup.LKCustomerContractOperationServiceOfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LKCustomerContractOperationServiceOfServiceImpl extends BaseServiceImpl<LKCustomerContractOperationService, Long> implements LKCustomerContractOperationServiceOfService {

    private final SiteDistributionRepository siteDistributionRepository;
    private final LKCustomerContractOperationServiceRepository lkCustomerContractOperationServiceRepository;

    @Override
    protected BaseRepository<LKCustomerContractOperationService, Long> getRepository() {
        return lkCustomerContractOperationServiceRepository;
    }

}