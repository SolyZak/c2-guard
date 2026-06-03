package com.eden.eden_crm_sec_crm_back.patrols.dtos.edit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Request body for PATCH /customer/patrols/{patrolId}. Carries name/frequency
 * changes and per-location add/remove task deltas. Copy-on-edit creates a new
 * Patrol version.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolEditRequest {

    @NotBlank
    private String name;

    @NotNull
    private String frequency;

    @NotNull
    private String frequencyRate;

    /** Existing locations with their task deltas. */
    @Valid
    @Builder.Default
    private List<LocationDelta> details = new ArrayList<>();

    /** New locations to add with initial tasks. */
    @Valid
    @Builder.Default
    private List<NewLocation> addLocations = new ArrayList<>();

    /** Locations to remove entirely (from this patrol and all services). */
    @Builder.Default
    private List<Long> removeLocationIds = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDelta {
        @NotNull
        private Long locationId;
        private Integer displayOrder;
        @Builder.Default
        private List<Long> addTaskIds = new ArrayList<>();
        @Builder.Default
        private List<Long> removeTaskIds = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewLocation {
        @NotNull
        private Long locationId;
        private Integer displayOrder;
        @Builder.Default
        private List<Long> taskIds = new ArrayList<>();
    }
}
