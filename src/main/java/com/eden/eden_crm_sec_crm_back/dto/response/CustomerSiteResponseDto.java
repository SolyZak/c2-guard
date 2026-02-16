package com.eden.eden_crm_sec_crm_back.dto.response;

public record CustomerSiteResponseDto(
        Long id,
        String name,
        Double latitude,
        Double longitude,
        Double tolerance,
        Long premiseId,
        String premiseName
) {}
