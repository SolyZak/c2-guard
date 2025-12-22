package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ValidateLocationRequest(
        @NotNull
        BigDecimal latitude,
        @NotNull
        BigDecimal longitude
) {}
