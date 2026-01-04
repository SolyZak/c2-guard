package com.eden.eden_crm_sec_crm_back.dto.response;
import java.math.BigDecimal;

public record PatrolLocationResponseDto(
        Long id,
        String name,
        String accessType,
        BigDecimal longitude,
        BigDecimal latitude,
        BigDecimal tolerance
) {
}
