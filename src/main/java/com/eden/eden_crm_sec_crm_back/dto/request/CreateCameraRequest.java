package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCameraRequest(
        @NotBlank(message = "{validation.name.not.empty}")
        @Size(max = 255, message = "{validation.name.max.length}")
        String name,

        @NotBlank(message = "{validation.ip.not.empty}")
        @Size(max = 20, message = "{validation.ip.max.length}")
        String ip,

        @NotNull(message = "{validation.vendor.id.not.null}")
        Long vendorId
) {}