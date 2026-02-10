package com.eden.eden_crm_sec_crm_back.dto.rbac.PredefinedData;

public record PredefinedPermissionCheckedDto(
        Long id,
        String nameEn,
        String nameAr,
        boolean checked
) {}