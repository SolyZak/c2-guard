package com.eden.eden_crm_sec_crm_back.controller.lookup;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.lookup.ServiceDetailsDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.ServiceDetailsMapper;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;


import com.eden.eden_crm_sec_crm_back.service.lookup.ServiceDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/customers/service-details")
@RequiredArgsConstructor
@Slf4j
public class ServiceDetailsController extends BaseController<ServiceDetails, ServiceDetailsDto, Long> {

    private final ServiceDetailsService serviceDetailsService;
    private final ServiceDetailsMapper serviceDetailsMapper;

    @Override
    protected BaseService<ServiceDetails, Long> getService() {
        log.info("Getting service");
        return serviceDetailsService;
    }

    @Override
    protected BaseMapper<ServiceDetails, ServiceDetailsDto> getMapper() {
        log.info("Getting mapper");
        return serviceDetailsMapper;
    }
}
