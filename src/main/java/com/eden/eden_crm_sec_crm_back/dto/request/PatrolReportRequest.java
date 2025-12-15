package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record PatrolReportRequest(
    @NotNull
    Long securityCompanyId,
    @NotNull
    Long contractId,
    @Nullable
    Set<Long> premiseIds,
    @Nullable
    Set<Long> patrolIds,
    @Nullable
    Set<Long> locationIds,
    @NotNull
    LocalDate fromDate,
    @NotNull
    LocalDate toDate
) {}
