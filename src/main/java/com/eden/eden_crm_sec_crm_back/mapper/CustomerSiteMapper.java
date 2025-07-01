package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.CustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteResponseDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.projections.GeneralDropdownProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerSiteMapper {
    CustomerSite toEntity(CustomerSiteRequestDto dto);

    CustomerSiteResponseDto fromEntity(CustomerSite entity);

    void updateEntityFromDto(UpdateCustomerSiteRequestDto dto, @MappingTarget CustomerSite entity);

    @Mapping(target = "id", source = "s.id")
    @Mapping(target = "name", source = "s.name")
    GeneralDropdown toDropdown(CustomerSite s);

    GeneralDropdown toDropdown(GeneralDropdownProjection s);
}
