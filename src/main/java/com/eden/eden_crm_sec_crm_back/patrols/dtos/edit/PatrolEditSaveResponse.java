package com.eden.eden_crm_sec_crm_back.patrols.dtos.edit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Response for PATCH /customer/patrols/{patrolId}. Contains the new patrol
 * version info and per-service impact summary.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolEditSaveResponse {
    private Long newPatrolId;
    private Long previousPatrolId;
    private LocalDate validFrom;
    private UUID editSessionId;
    private List<AffectedServiceEntry> affectedServices;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AffectedServiceEntry {
        private Long serviceId;
        private LocalDate cutoffDate;
        private boolean skippedTodayDueToAttendance;
    }
}
