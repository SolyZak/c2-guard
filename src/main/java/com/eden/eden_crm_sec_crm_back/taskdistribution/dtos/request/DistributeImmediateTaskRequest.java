package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record DistributeImmediateTaskRequest(
    @NotNull
    Long contractId,
    @NotNull
    Long serviceId,
    @NotNull
    Long siteId,
    @NotNull
    Long taskId,
    @NotNull
    OffsetDateTime startDateTime,
    @NotNull
    OffsetDateTime endDateTime,
    @NotEmpty
    List<Long> workforceIds,
    Long locationId,
    String locationName,
    BigDecimal latitude,
    BigDecimal longitude
) {
    @AssertTrue(message = "Start date must be before end date")
    public boolean isStartDateBeforeEndDate() {
        return startDateTime.isBefore(endDateTime);
    }

    @AssertTrue(message = "must provide either location id or location data")
    public boolean isLocationProvided() {
        return locationId != null || (locationName != null && latitude != null && longitude != null);
    }
}
