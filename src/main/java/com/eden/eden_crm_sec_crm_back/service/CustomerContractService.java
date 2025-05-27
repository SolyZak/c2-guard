package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractCustomDto;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractDetailsDto;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDetailsDTO;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;

import java.util.List;

public interface CustomerContractService extends BaseService<CustomerContract, Long> {
    List<CustomerContractCustomDto> getAllCustomerAgreements();
    CustomerContractDetailsDto getCustomerContractDetails(Long contractId);
    List<CustomerServiceDetailsDTO> getServicesForContract(String agreementNumber);
}
