package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.mapper.SiteDistributionMapper;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.service.SiteDistributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/customers/site-distribution")
@RequiredArgsConstructor
@Slf4j
public class SiteDistributionController extends BaseController<SiteDistribution, SiteDistributionDto, Long> {

    private final SiteDistributionService siteDistributionService;
    private final SiteDistributionMapper siteDistributionMapper;

    @Override
    protected BaseService<SiteDistribution, Long> getService() {
        log.info("Getting service");
        return siteDistributionService;
    }

    @Override
    protected BaseMapper<SiteDistribution, SiteDistributionDto> getMapper() {
        log.info("Getting mapper");
        return siteDistributionMapper;
    }



}
