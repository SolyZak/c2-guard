package com.eden.eden_crm_sec_crm_back.controller.lookup;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerAgreementServiceDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerAgreementServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerAgreementService;
import com.eden.eden_crm_sec_crm_back.service.lookup.LKCustomerAgreementServiceOfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/customers/agreement-service")
@RequiredArgsConstructor
@Slf4j
public class CustomerAgreementServiceController extends BaseController<LKCustomerAgreementService, LKCustomerAgreementServiceDto, Long> {

    private final LKCustomerAgreementServiceOfService customerAgreementServiceOfService;
    private final LKCustomerAgreementServiceMapper customerAgreementServiceMapper;

    @Override
    protected BaseService<LKCustomerAgreementService, Long> getService() {
        log.info("Getting service");
        return customerAgreementServiceOfService;
    }

    @Override
    protected BaseMapper<LKCustomerAgreementService, LKCustomerAgreementServiceDto> getMapper() {
        log.info("Getting mapper");
        return customerAgreementServiceMapper;
    }
}
