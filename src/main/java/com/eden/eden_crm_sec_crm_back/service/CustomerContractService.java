package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractDetailsData;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractRowDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractServiceDetailsData;
import com.eden.eden_crm_sec_crm_back.dto.response.DistributedOperationSite;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.time.LocalDate;
import java.util.List;

public interface CustomerContractService {
    String createAgreement(AddContractDto dto);
    PaginateResponse<ContractRowDto> paginateMyContracts(String search, LocalDate from, LocalDate to, int page, int size);
    List<ContractRowDto> listMyDraftedContracts();
    List<ContractServiceDetailsData> contractServicesList(Long contractId);
    List<ContractRowDto> listAllMyContracts();
    List<GeneralDropdown> availableOperationSitesListWithDistributedContracts(Long contractId, Long serviceId);
    List<DistributedOperationSite> distributedOperationSites(Long contractId, Long lkCustomerContractServiceId);
    ContractDetailsData getCustomerContractDetails(Long contractId);
}