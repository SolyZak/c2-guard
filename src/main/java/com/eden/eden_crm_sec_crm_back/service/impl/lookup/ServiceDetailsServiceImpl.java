package com.eden.eden_crm_sec_crm_back.service.impl.lookup;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.repository.lookup.ServiceDetailsRepository;
import com.eden.eden_crm_sec_crm_back.service.lookup.ServiceDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceDetailsServiceImpl extends BaseServiceImpl<ServiceDetails, Long> implements ServiceDetailsService {

    private final ServiceDetailsRepository lkServiceDetailsRepository;
    @Override
    protected BaseRepository<ServiceDetails, Long> getRepository() {
        return lkServiceDetailsRepository;
    }

}
