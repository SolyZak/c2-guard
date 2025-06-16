package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;

import java.util.List;

public interface DropdownService {
    List<GeneralDropdown> customerContractedSecurityCompaniesDropdown(Long customerId);
}
