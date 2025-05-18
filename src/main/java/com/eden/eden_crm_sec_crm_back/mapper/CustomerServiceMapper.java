package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDTO;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CustomerServiceMapper extends BaseMapper<CustomerService, CustomerServiceDTO> {
    @Override
    @Mapping(target = "serviceDetails", source = "serviceDetails")
    CustomerServiceDTO map(CustomerService entity);

    @Override
    @Mapping(target = "serviceDetails", source = "serviceDetails")
    CustomerService unMap(CustomerServiceDTO dto);
}
