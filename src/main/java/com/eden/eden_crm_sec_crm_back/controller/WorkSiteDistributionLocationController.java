package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.WorkSiteDistributionLocationDto;
import com.eden.eden_crm_sec_crm_back.mapper.WorkSiteDistributionLocationMapper;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.WorkSiteDistributionLocation;
import com.eden.eden_crm_sec_crm_back.service.WorkSiteDistributionLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/customers/work-site-distribution-location")
@RequiredArgsConstructor
@Slf4j
public class WorkSiteDistributionLocationController extends BaseController<WorkSiteDistributionLocation, WorkSiteDistributionLocationDto, Long> {

    private final WorkSiteDistributionLocationService workSiteDistributionLocationService;
    private final WorkSiteDistributionLocationMapper workSiteDistributionLocationMapper;

    @Override
    protected BaseService<WorkSiteDistributionLocation, Long> getService() {
        log.info("Getting service");
        return workSiteDistributionLocationService;
    }

    @Override
    protected BaseMapper<WorkSiteDistributionLocation, WorkSiteDistributionLocationDto> getMapper() {
        log.info("Getting mapper");
        return workSiteDistributionLocationMapper;
    }



}
