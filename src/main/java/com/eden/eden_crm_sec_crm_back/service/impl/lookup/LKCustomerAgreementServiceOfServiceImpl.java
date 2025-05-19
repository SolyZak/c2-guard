package com.eden.eden_crm_sec_crm_back.service.impl.lookup;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerAgreementService;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerAgreementServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.lookup.LKCustomerAgreementServiceOfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LKCustomerAgreementServiceOfServiceImpl extends BaseServiceImpl<LKCustomerAgreementService, Long> implements LKCustomerAgreementServiceOfService {

    private final LKCustomerAgreementServiceRepository  lkCustomerAgreementServiceRepository;
    @Override
    protected BaseRepository<LKCustomerAgreementService, Long> getRepository() {
        return   lkCustomerAgreementServiceRepository;
    }
}
