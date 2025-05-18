package com.eden.eden_crm_sec_crm_back.controller.lookup;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.lookup.SecurityCompanyDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.ServiceDetailsDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.SecurityCompanyMapper;
import com.eden.eden_crm_sec_crm_back.models.lookup.SecurityCompany;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.service.lookup.SecurityCompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/customers/security-company")
@RequiredArgsConstructor
@Slf4j
public class SecurityCompanyController extends BaseController<SecurityCompany, SecurityCompanyDto, Long> {

    private final SecurityCompanyService securityCompanyService;
    private final SecurityCompanyMapper securityCompanyMapper;

    @Override
    protected BaseService<SecurityCompany, Long> getService() {
        log.info("Getting service");
        return securityCompanyService;
    }

    @Override
    protected BaseMapper<SecurityCompany, SecurityCompanyDto> getMapper() {
        log.info("Getting mapper");
        return securityCompanyMapper;
    }
}
