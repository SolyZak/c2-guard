package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentPatchResponse {
    private UUID editSessionId;
    private LocalDate cutoffDate;
    private boolean skippedTodayDueToAttendance;
    private List<AssignmentLookupResponse.AssignmentLocationEntry> locations;
}
