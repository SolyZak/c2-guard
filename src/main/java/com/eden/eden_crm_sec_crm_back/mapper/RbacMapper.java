package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.rbac.PermissionDto;
import com.eden.eden_crm_sec_crm_back.dto.rbac.RoleDto;
import com.eden.eden_crm_sec_crm_back.models.PermissionEntity;
import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RbacMapper {
    PermissionDto toDto(PermissionEntity e);
    RoleDto toDto(RoleEntity e);
}