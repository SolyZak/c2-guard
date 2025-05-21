package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractCustomDto;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractDto;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerContractMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/customers/contracts")
@RequiredArgsConstructor
@Slf4j
public class CustomerContractController extends BaseController<CustomerContract, CustomerContractDto, Long> {

    private final CustomerContractService customerContractService;
    private final CustomerContractMapper customerContractMapper;

    @Override
    protected BaseService<CustomerContract, Long> getService() {
        log.info("Getting service");
        return customerContractService;
    }

    @Override
    protected BaseMapper<CustomerContract, CustomerContractDto> getMapper() {
        log.info("Getting mapper");
        return customerContractMapper;
    }
    @GetMapping("/all-agreements-details")
    public List<CustomerContractCustomDto> getAllCustomerAgreement() {
        return customerContractService.getAllCustomerAgreements();
    }


}
