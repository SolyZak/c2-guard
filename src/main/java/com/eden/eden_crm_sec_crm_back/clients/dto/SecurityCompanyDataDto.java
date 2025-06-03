package com.eden.eden_crm_sec_crm_back.clients.dto;

public record SecurityCompanyDataDto(
        Long id,
        String name,
        String email,
        String phone,
        String countryCode
) {
}
