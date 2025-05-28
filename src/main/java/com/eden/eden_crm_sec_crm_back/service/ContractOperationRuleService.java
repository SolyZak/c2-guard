package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.ContractOperationRuleDTO;
import jakarta.validation.Valid;

public interface ContractOperationRuleService {
    String changeContractRule(@Valid ContractOperationRuleDTO dto);
}
