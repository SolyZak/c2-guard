package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;

import java.util.List;

public interface DropdownService {
    List<GeneralDropdown> customerContractedSecurityCompaniesDropdown(Long customerId);
    List<GeneralDropdown> myContractsDropdown(Long customerId, Long securityCompanyId);
    List<GeneralDropdown> myOperationSitesDropdown(Long customerId, Long securityCompanyId, Long contractId);
}
