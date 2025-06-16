package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerContractMapper;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.service.DropdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DropdownServiceImpl implements DropdownService {

    private final CustomerContractRepository customerContractRepository;
    private final CustomerContractMapper customerContractMapper;

    @Override
    public List<GeneralDropdown> customerContractedSecurityCompaniesDropdown(Long customerId) {
        return customerContractRepository.securityCompanies(customerId).stream().map(customerContractMapper::toDropdown).toList();
    }

    @Override
    public List<GeneralDropdown> myContractsDropdown(Long customerId, Long securityCompanyId) {
        // todo must include status once we impl status controlling
        return customerContractRepository.contractsDropdown(customerId, securityCompanyId).stream().map(customerContractMapper::toDropdown).toList();
    }
}
