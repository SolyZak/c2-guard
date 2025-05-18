package com.eden.eden_crm_sec_crm_back.service.impl.lookup;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.lookup.SecurityCompany;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.repository.lookup.SecurityCompanyRepository;
import com.eden.eden_crm_sec_crm_back.service.lookup.SecurityCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SecurityCompanyServiceImpl extends BaseServiceImpl<SecurityCompany, Long> implements SecurityCompanyService {

    private final SecurityCompanyRepository securityCompanyRepository;
    @Override
    protected BaseRepository<SecurityCompany, Long> getRepository() {
        return securityCompanyRepository;
    }

}
