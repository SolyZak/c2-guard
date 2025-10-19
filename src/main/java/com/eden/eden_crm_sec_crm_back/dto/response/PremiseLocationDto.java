package com.eden.eden_crm_sec_crm_back.dto.response;

public record PremiseLocationDto(
        Long id,
        String name,
        String accessType,
        String premiseName,
        String qrCode
) {
}
