package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record ExecuteDistributedTaskRequest(
        @NotNull
        Long executionSlotId,
        @NotEmpty
        @Valid
        List<SubmittedCheckDTO> checks
) {}