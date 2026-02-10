package com.eden.eden_crm_sec_crm_back.patrols.dtos.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record PatrolReportRequest(
    @NotNull
    Long securityCompanyId,
    @NotNull
    Long contractId,
    @NotNull
    LocalDate fromDate,
    @NotNull
    LocalDate toDate,
    @Nullable
    Set<Long> premiseIds,
    @Nullable
    Set<Long> patrolIds,
    @Nullable
    Set<Long> locationIds
) {
    @AssertTrue(message = "From date must be before to date or equals")
    public boolean isFromDateBeforeToDateOrEquals() {
        if (fromDate.equals(toDate))
            return true;

        return fromDate.isBefore(toDate);
    }
}
