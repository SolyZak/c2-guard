package com.eden.eden_crm_sec_crm_back.dto.rbac.PredefinedData;

import java.util.List;

public record PredefinedActivityCheckedDto(
        Integer id,
        String nameEn,
        String nameAr,
        List<PredefinedPermissionCheckedDto> permissions
) {}