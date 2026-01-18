package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record WorkforceLocationRequest(
    @NonNull
    BigDecimal longitude,
    @NonNull
    BigDecimal latitude
) {}
