package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDTO;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CustomerServiceMapper extends BaseMapper<CustomerService, CustomerServiceDTO> {

    @Override
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "serviceDetails", target = "serviceDetails")
    CustomerServiceDTO map(CustomerService entity);

    @Override
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "serviceDetails", source = "serviceDetails")
    CustomerService unMap(CustomerServiceDTO dto);
}
