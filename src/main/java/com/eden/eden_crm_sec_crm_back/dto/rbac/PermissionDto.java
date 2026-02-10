package com.eden.eden_crm_sec_crm_back.dto.rbac;

public record PermissionDto(
        Long id,
        String keycloakRoleName,
        String nameEn,
        String nameAr
) {}