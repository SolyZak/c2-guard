package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateLocationRequest {
    private String locationName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal tolerance;
}