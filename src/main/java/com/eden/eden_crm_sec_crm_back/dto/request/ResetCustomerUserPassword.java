package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetCustomerUserPassword(
        @NotBlank(message = "{validation.password.not-empty}")
        @NotNull(message = "{validation.password.not-empty}")
        String password
) {}
