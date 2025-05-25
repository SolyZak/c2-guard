package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.SiteDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class SiteDistributionServiceImpl extends BaseServiceImpl<SiteDistribution, Long> implements SiteDistributionService {

    private final SiteDistributionRepository  siteDistributionRepository;
    @Override
    protected BaseRepository<SiteDistribution, Long> getRepository() {
        return  siteDistributionRepository;
    }


    @Override
    public SiteDistribution insert(SiteDistribution entity) {
        if (entity.getOperationServices() != null) {
            entity.getOperationServices().forEach(op -> op.setSiteDistribution(entity));
        }
        return super.insert(entity);
    }
}
