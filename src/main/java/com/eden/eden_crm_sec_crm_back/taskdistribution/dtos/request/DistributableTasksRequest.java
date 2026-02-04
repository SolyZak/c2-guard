package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DistributableTasksRequest(
    @NotNull
    Long patrolId,
    @NotNull
    Long locationId
) {}
