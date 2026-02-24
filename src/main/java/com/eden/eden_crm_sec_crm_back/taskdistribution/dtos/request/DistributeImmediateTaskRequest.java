package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

@Builder
public record DistributeImmediateTaskRequest(

    // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
    // Legacy field pointing to the old `task` table.
    // @NotNull removed so clients using the new taskDefinitionId path don't need to send this.
    // CLEANUP: remove this field entirely after Phase E cleanup migration.
    Long taskId,
    // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // New field targeting the task_management module via the ACL (TaskPresenter).
    // CLEANUP: add @NotNull here once taskId is retired and task_definition_id is NOT NULL in DB.
    Long taskDefinitionId,
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    @NotNull
    OffsetDateTime startDateTime,
    @NotNull
    OffsetDateTime endDateTime,
    @NotEmpty
    Set<Long> workforceIds,
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

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Cross-field validation: at least one task reference must be provided.
    // CLEANUP: remove this method and replace with @NotNull on taskDefinitionId once taskId is retired.
    @AssertTrue(message = "must provide either taskId or taskDefinitionId")
    public boolean isTaskReferenceProvided() {
        return taskId != null || taskDefinitionId != null;
    }
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
}
