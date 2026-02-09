package com.eden.eden_crm_sec_crm_back.dto.rbac.PredefinedData;

import java.util.List;

public record PredefinedRoleWithChecksDto(
        Integer roleId,
        String roleName,
        String description,
        List<PredefinedClassCheckedDto> permissionsTree
) {}