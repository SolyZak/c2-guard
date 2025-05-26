package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.WorkSiteDistributionLocation;
import com.eden.eden_crm_sec_crm_back.repository.WorkSiteDistributionLocationRepository;
import com.eden.eden_crm_sec_crm_back.service.WorkSiteDistributionLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class WorkSiteDistributionLocationServiceImpl extends BaseServiceImpl<WorkSiteDistributionLocation, Long> implements WorkSiteDistributionLocationService {

    private final WorkSiteDistributionLocationRepository siteDistributionLocationRepository;
    @Override
    protected BaseRepository<WorkSiteDistributionLocation, Long> getRepository() {
        return siteDistributionLocationRepository;
    }

    @Override
    public WorkSiteDistributionLocation insert(WorkSiteDistributionLocation entity) {
        return super.insert(entity);
    }
}
