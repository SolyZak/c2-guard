package com.eden.eden_crm_sec_crm_back.dto.response;

import java.math.BigDecimal;

public record PremiseLocationDto(
        Long id,
        String name,
        String accessType,
        String premiseName,
        BigDecimal premiseLatitude,
        BigDecimal premiseLongitude,
        String qrCode,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal tolerance
) {
}
