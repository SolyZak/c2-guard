package com.eden.eden_crm_sec_crm_back.controller.lookup;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractOperationServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.service.lookup.LKCustomerContractOperationServiceOfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/customers/contract-operation")
@RequiredArgsConstructor
@Slf4j
public class LKCustomerContractOperationServiceController extends BaseController<LKCustomerContractOperationService, LKCustomerContractOperationServiceDto, Long> {

    private final LKCustomerContractOperationServiceOfService lkCustomerContractOperationServiceOfService;
    private final LKCustomerContractOperationServiceMapper lkCustomerContractOperationServiceMapper;

    @Override
    protected BaseService<LKCustomerContractOperationService, Long> getService() {
        log.info("Getting service");
        return lkCustomerContractOperationServiceOfService;
    }

    @Override
    protected BaseMapper<LKCustomerContractOperationService, LKCustomerContractOperationServiceDto> getMapper() {
        log.info("Getting mapper");
        return lkCustomerContractOperationServiceMapper;
    }
}
