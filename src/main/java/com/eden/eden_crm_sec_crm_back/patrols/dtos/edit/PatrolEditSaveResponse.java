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
 * Response for PATCH /customer/patrols/{patrolId}. The patrol is edited in
 * place, so {@code newPatrolId} and {@code previousPatrolId} both carry the
 * edited patrol's id (kept for backward compatibility); {@code validFrom} is the
 * effective cutoff date of the change. Also includes the per-service impact
 * summary.
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
