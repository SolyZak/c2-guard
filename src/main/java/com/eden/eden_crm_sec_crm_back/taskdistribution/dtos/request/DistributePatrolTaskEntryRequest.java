package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DistributePatrolTaskEntryRequest(
    @NotNull
    Long siteId,
    @NotNull
    Long serviceTimeId,
    @NotNull
    LocalDate startDate,
    @NotEmpty
    List<Long> patrolDetailIds
) {}
