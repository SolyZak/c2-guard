package com.eden.eden_crm_sec_crm_back.controller.lookup;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractServiceDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.service.lookup.LKCustomerContractServiceOfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/customers/contract-service")
@RequiredArgsConstructor
@Slf4j
public class CustomerContractServiceController extends BaseController<LKCustomerContractService, LKCustomerContractServiceDto, Long> {

    private final LKCustomerContractServiceOfService customerAgreementServiceOfService;
    private final LKCustomerContractServiceMapper customerAgreementServiceMapper;

    @Override
    protected BaseService<LKCustomerContractService, Long> getService() {
        log.info("Getting service");
        return customerAgreementServiceOfService;
    }

    @Override
    protected BaseMapper<LKCustomerContractService, LKCustomerContractServiceDto> getMapper() {
        log.info("Getting mapper");
        return customerAgreementServiceMapper;
    }


    @GetMapping("/details")
    public ApiResponse<List<LKCustomerContractServiceDto>> getByAgreementAndService(
            @RequestParam("customerContractId") Long agreementId,
            @RequestParam("serviceId") Long serviceId) {

        List<LKCustomerContractServiceDto> result = customerAgreementServiceOfService.getByAgreementWithServices(agreementId, serviceId);
        return ApiResponse.ok(result);
    }
}
