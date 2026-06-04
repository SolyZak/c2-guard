package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentLookupResponse {

    private boolean exists;
    private Long serviceId;
    private Long patrolId;
    private Long serviceTimeId;
    private Long siteId;
    private LocalDate startDate;
    private List<AssignmentLocationEntry> locations;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentLocationEntry {
        private Long locationId;
        private String locationName;
        private String premiseName;
        private List<Long> taskIds;
    }
}
