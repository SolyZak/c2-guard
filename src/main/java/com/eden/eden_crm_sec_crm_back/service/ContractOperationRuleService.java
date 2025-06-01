package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.ContractOperationRuleDTO;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractWithRules;
import jakarta.validation.Valid;

import java.util.List;

public interface ContractOperationRuleService {
    List<ContractWithRules> contractListWithRules();
    String changeContractRule(@Valid ContractOperationRuleDTO dto);
}
