package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.ContractOperationRuleDTO;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractWithRules;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerContractMapper;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationRule;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationRuleRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.service.ContractOperationRuleService;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractOperationRuleServiceImpl implements ContractOperationRuleService {
    private final ContractOperationRuleRepository contractOperationRuleRepository;
    private final CustomerContractRepository contractRepository;
    private final CustomerService customerService;
    private final CustomerContractMapper contractMapper;

    @Override
    public List<ContractWithRules> contractListWithRules() {
        return contractRepository.listByCustomerIdWithRules(customerService.getLoggedInCustomer().getId())
                .stream().map(contractMapper::toContractWithRules)
                .toList();
    }

    @Override
    public String changeContractRule(ContractOperationRuleDTO dto) {
        CustomerContract contract = contractRepository.findByIdAndCustomerId(dto.getContractId(), Utils.getLoggedInCustomerId())
                .orElseThrow(
                        () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contractId")}), HttpStatus.NOT_FOUND)
                );
        Optional<ContractOperationRule> contractOperationRuleExists = contractOperationRuleRepository.findContractRule(dto.getContractId());
        ContractOperationRule contractOperationRule = contractOperationRuleExists.orElseGet(ContractOperationRule::new);

        contractOperationRule.setCustomerAgreement(contract);
        contractOperationRule.setAllowCheckInBefore(dto.isAllowCheckInBefore());
        contractOperationRule.setCheckInBeforeMinutes(dto.getCheckInBeforeMinutes());
        contractOperationRule.setAllowCheckInAfter(dto.isAllowCheckInAfter());
        contractOperationRule.setCheckInAfterMinutes(dto.getCheckInAfterMinutes());
        contractOperationRule.setAllowCheckOutAfter(dto.isAllowCheckOutAfter());
        contractOperationRule.setCheckOutAfterMinutes(dto.getCheckOutAfterMinutes());
        contractOperationRule.setPresenceMode(dto.getPresenceMode());
        contractOperationRuleRepository.save(contractOperationRule);

        return MessageUtil.getMessage("contract-operation-rule.changed");
    }
}
