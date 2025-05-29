package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractRowDto;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import jakarta.validation.Valid;

import java.time.LocalDate;

public interface CustomerContractService {
//    List<CustomerContractCustomDto> getAllCustomerAgreements();
//    CustomerContractDetailsDto getCustomerContractDetails(Long contractId);
//    List<CustomerServiceDetailsDTO> getServicesForContract(String agreementNumber);

    String createAgreement(@Valid AddContractDto dto);
    PaginateResponse<ContractRowDto> paginateMyContracts(String search, LocalDate from, LocalDate to, int page, int size);
}
