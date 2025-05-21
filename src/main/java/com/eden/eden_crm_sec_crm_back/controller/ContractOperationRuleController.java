package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.controller.BaseController;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.ContractOperationRuleDTO;
import com.eden.eden_crm_sec_crm_back.mapper.ContractOperationRuleMapper;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationRule;
import com.eden.eden_crm_sec_crm_back.service.ContractOperationRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/customers/contract-operation-rules")
@RequiredArgsConstructor
@Slf4j
public class ContractOperationRuleController extends BaseController<ContractOperationRule, ContractOperationRuleDTO, Long> {

    private final ContractOperationRuleService  contractOperationRuleService;
    private final ContractOperationRuleMapper contractOperationRuleMapper;

    @Override
    protected BaseService<ContractOperationRule, Long> getService() {
        log.info("Getting service");
        return contractOperationRuleService;
    }

    @Override
    protected BaseMapper<ContractOperationRule, ContractOperationRuleDTO> getMapper() {
        log.info("Getting mapper");
        return contractOperationRuleMapper;
    }



}
