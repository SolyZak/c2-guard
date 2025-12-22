package com.eden.eden_crm_sec_crm_back.dto.response;

import java.math.BigDecimal;

public record LocationResponseDto(
        Long locationId,
        String locationName,
        BigDecimal longitude,
        BigDecimal latitude
) {
}
