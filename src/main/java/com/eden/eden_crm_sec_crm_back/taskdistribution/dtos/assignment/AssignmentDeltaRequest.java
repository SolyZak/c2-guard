package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Body of PATCH /customer/task-distribution/patrol/assignment.
 *
 * <p>At least one of {@link PerLocation#getAddTaskIds()},
 * {@link PerLocation#getRemoveTaskIds()} or {@link #removeLocationIds} must be
 * non-empty across the whole request (else NO_CHANGES). {@code perLocation}
 * entries with both arrays empty are tolerated as no-ops.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDeltaRequest {

    @NotNull
    private Long serviceId;

    @NotNull
    private Long patrolId;

    @NotNull
    private Long serviceTimeId;

    @NotNull
    private Long siteId;

    @Valid
    @Builder.Default
    private List<PerLocation> perLocation = new ArrayList<>();

    @Builder.Default
    private List<Long> removeLocationIds = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerLocation {
        @NotNull
        private Long locationId;

        @Builder.Default
        private List<Long> addTaskIds = new ArrayList<>();

        @Builder.Default
        private List<Long> removeTaskIds = new ArrayList<>();
    }
}
