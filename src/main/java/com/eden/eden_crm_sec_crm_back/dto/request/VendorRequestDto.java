package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VendorRequestDto(
        @NotBlank(message = "{validation.name.not.empty}")
        @Size(max = 255, message = "{validation.name.max.length}")
        String name
) {}

