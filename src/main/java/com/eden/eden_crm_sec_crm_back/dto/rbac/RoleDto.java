package com.eden.eden_crm_sec_crm_back.dto.rbac;

import java.util.List;

public record RoleDto(
        Integer id,
        String name,
        String description,
        List<PermissionDto> permissions
) {}