package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

import java.util.Set;

@Builder
public record PatrolReportRequest(
    @NotNull
    Long securityCompanyId,
    @NotNull
    Long contractId,
    @Nullable
    Long premiseId,
    @Nullable
    Long patrolId,
    @Nullable
    Set<Long> locationIds,
    @NotNull
    @Range(min = 1, max = 12)
    Integer month
) {}
