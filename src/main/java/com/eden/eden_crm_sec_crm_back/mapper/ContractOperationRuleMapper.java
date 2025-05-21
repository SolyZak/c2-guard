package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.ContractOperationRuleDTO;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationRule;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ContractOperationRuleMapper extends BaseMapper<ContractOperationRule, ContractOperationRuleDTO> {

    @Override
    @Mapping(source = "customerAgreement.id", target = "customerAgreementId")
    ContractOperationRuleDTO map(ContractOperationRule entity);

    @Override
    @Mapping(source = "customerAgreementId", target = "customerAgreement")
    ContractOperationRule unMap(ContractOperationRuleDTO dto);

    default CustomerContract map(Long id) {
        if (id == null) {
            return null;
        }
        CustomerContract agreement = new CustomerContract();
        agreement.setId(id);
        return agreement;
    }
}
