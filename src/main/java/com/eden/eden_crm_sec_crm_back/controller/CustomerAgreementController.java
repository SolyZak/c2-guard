package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.CustomerAgreementDTO;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerAgreementMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerAgreement;
import com.eden.eden_crm_sec_crm_back.service.CustomerAgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/customers/agreements")
@RequiredArgsConstructor
@Slf4j
public class CustomerAgreementController extends BaseController<CustomerAgreement, CustomerAgreementDTO, Long> {

    private final CustomerAgreementService customerAgreementService;
    private final CustomerAgreementMapper customerAgreementMapper;

    @Override
    protected BaseService<CustomerAgreement, Long> getService() {
        log.info("Getting service");
        return customerAgreementService;
    }

    @Override
    protected BaseMapper<CustomerAgreement, CustomerAgreementDTO> getMapper() {
        log.info("Getting mapper");
        return customerAgreementMapper;
    }


}
