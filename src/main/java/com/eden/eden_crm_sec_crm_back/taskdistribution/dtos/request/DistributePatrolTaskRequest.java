package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record DistributePatrolTaskRequest(
    @NotNull
    Long contractId,
    @NotNull
    Long serviceId,
    @NotEmpty
    @Valid
    List<DistributePatrolTaskEntryRequest> distributions
) {}
