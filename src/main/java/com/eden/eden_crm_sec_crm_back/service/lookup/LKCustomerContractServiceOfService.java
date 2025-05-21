package com.eden.eden_crm_sec_crm_back.service.lookup;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractServiceDto;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;

import java.util.List;

public interface LKCustomerContractServiceOfService extends BaseService<LKCustomerContractService, Long> {
     List<LKCustomerContractServiceDto> getByAgreementWithServices(Long agreementId, Long serviceId) ;


    }
