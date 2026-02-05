package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AvailableServiceTimesRequest(
    @NotNull
    Long contractId,
    @NotNull
    Long serviceId,
    @NotNull
    Long siteId,
    @NotNull
    Long patrolId
) {}
