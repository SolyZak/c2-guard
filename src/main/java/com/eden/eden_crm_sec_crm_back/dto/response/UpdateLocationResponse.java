package com.eden.eden_crm_sec_crm_back.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  // This will exclude null fields from JSON response
public class UpdateLocationResponse {
    private String locationName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal tolerance;
}