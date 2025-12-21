package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public record LocationDto(
        Long locationId,
        String locationName,
        BigDecimal longitude,
        BigDecimal latitude
) {
}
