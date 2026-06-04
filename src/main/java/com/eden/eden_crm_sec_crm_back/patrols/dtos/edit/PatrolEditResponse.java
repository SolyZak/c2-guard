package com.eden.eden_crm_sec_crm_back.patrols.dtos.edit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Response for GET /customer/patrols/{patrolId}/edit. Returns the patrol
 * definition prefilled for the edit screen, with locations grouped and their
 * task lists.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolEditResponse {
    private Long patrolId;
    private String name;
    private String frequency;
    private String frequencyRate;
    private LocalDate validFrom;
    private List<PatrolEditLocationEntry> details;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatrolEditLocationEntry {
        private Long locationId;
        private String locationName;
        private String premiseName;
        private Integer displayOrder;
        private List<PatrolEditTaskEntry> tasks;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatrolEditTaskEntry {
        private Long taskDefinitionId;
        private String taskName;
        private Long patrolDetailId;
    }
}
