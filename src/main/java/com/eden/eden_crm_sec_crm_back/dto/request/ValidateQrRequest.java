package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ValidateQrRequest(
        @NotBlank
        String payload,

        @NotNull
        Long taskDefinitionId,

        Long patrolDistributionId,
        Long immediateDistributionId
) {
    @AssertTrue(message = "Must provide either patrolDistributionId or immediateDistributionId")
    public boolean isDistributionProvided() {
        boolean hasPatrol = patrolDistributionId != null && patrolDistributionId > 0;
        boolean hasImmediate = immediateDistributionId != null && immediateDistributionId > 0;
        return hasPatrol || hasImmediate;
    }
}