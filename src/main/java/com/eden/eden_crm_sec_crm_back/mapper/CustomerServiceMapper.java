package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.AddServiceDetailsDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDataDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDetailsDropdownDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerServiceMapper {
    @Mapping(target = "serviceName", source = "name")
    CustomerService toEntity(AddServiceDto dto);

    @Mapping(target = "customerService", source = "s")
    ServiceDetails toEntity(AddServiceDetailsDto dto, CustomerService s);

    @Mapping(target = "name", source = "serviceName")
    @Mapping(target = "serviceDetails", source = "serviceDetails")
    ServiceDataDto toServiceDataDto(CustomerService entity);

    @Mapping(target = "serviceName", source = "e.customerService.serviceName")
    @Mapping(target = "serviceId", source = "e.customerService.id")
    ServiceDetailsDropdownDto toServiceDetailsDropdownDto(ServiceDetails e);
}
