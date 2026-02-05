package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;

public record CustomerActivationRequestDto(
        @NotNull Boolean active
) {}