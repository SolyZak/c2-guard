package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.SecurityCompanyDto;
import com.eden.eden_crm_sec_crm_back.models.lookup.SecurityCompany;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SecurityCompanyMapper extends BaseMapper<SecurityCompany, SecurityCompanyDto> {
}
