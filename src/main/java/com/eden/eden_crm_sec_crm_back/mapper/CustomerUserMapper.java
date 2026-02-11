package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.AddCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserData;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerUserMapper {
    @Mapping(target = "id", ignore = true)
    CustomerUser toEntity(AddCustomerUserDto dto);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    CustomerUserData toResponse(CustomerUser entity);
}
