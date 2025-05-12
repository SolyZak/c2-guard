package com.eden.eden_crm_sec_crm_back.dto.request;

public record CustomerSiteRequestDto(
        String name,
        Long customerId,
        Double latitude,
        Double longitude,
        Double tolerance
) {}
