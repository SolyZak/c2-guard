package com.eden.eden_crm_sec_crm_back.clients.dto;

public record WorkforceDataDto(
        Long id,
        String name,
        String code,
        String email,
        String phone,
        String countryCode
) {
}
