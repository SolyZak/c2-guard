package com.eden.eden_crm_sec_crm_back.dto.rbac.PredefinedData;

import java.util.List;

public record PredefinedClassCheckedDto(
        Integer id,
        String nameEn,
        String nameAr,
        List<PredefinedActivityCheckedDto> activities
) {}