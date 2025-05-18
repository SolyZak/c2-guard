package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.ServiceDetailsDto;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ServiceDetailsMapper extends BaseMapper<ServiceDetails, ServiceDetailsDto> {
}
