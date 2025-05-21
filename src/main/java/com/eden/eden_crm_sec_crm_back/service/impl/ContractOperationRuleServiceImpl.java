package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationRule;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationRuleRepository;
import com.eden.eden_crm_sec_crm_back.service.ContractOperationRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractOperationRuleServiceImpl extends BaseServiceImpl<ContractOperationRule, Long> implements ContractOperationRuleService {
    private final ContractOperationRuleRepository contractOperationRuleRepository;


    @Override
    protected BaseRepository<ContractOperationRule, Long> getRepository() {
        return contractOperationRuleRepository;
    }

}
