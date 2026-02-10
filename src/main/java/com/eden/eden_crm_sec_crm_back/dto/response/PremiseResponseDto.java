package com.eden.eden_crm_sec_crm_back.dto.response;


import java.math.BigDecimal;

public record PremiseResponseDto(
        Long id,
        String name,
        String code,
        BigDecimal longitude,
        BigDecimal latitude
) {
}
