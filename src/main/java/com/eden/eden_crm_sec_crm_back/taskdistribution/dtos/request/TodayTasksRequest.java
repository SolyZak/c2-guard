package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record TodayTasksRequest(
    @NotNull
    Long contractId,
    @NotNull
    Long serviceId,
    @NotNull
    Long serviceTimeId,
    @NotNull
    @Positive
    Integer slotNumber
) {}
