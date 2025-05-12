package com.eden.eden_crm_sec_crm_back.dto.request;

public record UpdateCustomerSiteRequestDto(
        String name,
        Double latitude,
        Double longitude,
        Double tolerance
) {}
