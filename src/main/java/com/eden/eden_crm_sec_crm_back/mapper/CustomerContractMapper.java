package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = LKCustomerContractServiceMapper.class)
public interface CustomerContractMapper extends BaseMapper<CustomerContract, CustomerContractDto> {
    @Override
    @Mapping(target = "lkCustomerContractServiceDto", source = "customerContractServices")
    CustomerContractDto map(CustomerContract entity);

    @Override
    @Mapping(target = "customerContractServices", source = "lkCustomerContractServiceDto")
    CustomerContract unMap(CustomerContractDto dto);
}

