package com.eden.eden_crm_sec_crm_back.dto.rbac;

import java.util.List;

public record PermissionClassDto(
        Integer id,
        String nameEn,
        String nameAr,
        List<PermissionDto> permissions
) {}