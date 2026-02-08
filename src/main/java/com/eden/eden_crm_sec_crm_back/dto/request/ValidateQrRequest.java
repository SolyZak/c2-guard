package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ValidateQrRequest(
    @NotBlank
    String payload,
    @NotNull
    Long patrolDistributionId,
    @NotNull
    Long taskId
) {}