package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractDetailsDto;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        uses = {
                ContractOperationRuleMapper.class,
                CustomerServiceMapper.class,
                SiteDistributionMapper.class
        }
)
public interface CustomerContractDetailsMapper extends BaseMapper<CustomerContract, CustomerContractDetailsDto> {


    @Override
    @Mapping(target = "contractOperationRuleDTO", source = "customerAgreement")
    CustomerContractDetailsDto map(CustomerContract entity);

    @Override
    @Mapping(target = "customerAgreement", source = "contractOperationRuleDTO")
    CustomerContract unMap(CustomerContractDetailsDto dto);
}



