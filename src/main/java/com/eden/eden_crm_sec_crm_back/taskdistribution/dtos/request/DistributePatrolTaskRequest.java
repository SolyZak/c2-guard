package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DistributePatrolTaskRequest(
    @NotNull
    Long contractId,
    @NotNull
    Long serviceId,
    @NotNull
    Long siteId,
    @NotNull
    Long serviceTimeId,
    @NotEmpty
    List<Long> patrolDetailIds,
    @NotNull
    LocalDate startDate,
    @NotNull
    @Positive
    Integer slotNumber
) {}
