package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationRequestDto {
    String locationName;
    String accessType;
    BigDecimal longitude;
    BigDecimal latitude;
    BigDecimal tolerance;
}
