package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import jakarta.validation.Valid;

public interface CustomerContractService {
//    List<CustomerContractCustomDto> getAllCustomerAgreements();
//    CustomerContractDetailsDto getCustomerContractDetails(Long contractId);
//    List<CustomerServiceDetailsDTO> getServicesForContract(String agreementNumber);

    String createAgreement(@Valid AddContractDto dto);
}
