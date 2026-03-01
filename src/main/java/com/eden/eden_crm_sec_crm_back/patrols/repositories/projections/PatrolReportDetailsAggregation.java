package com.eden.eden_crm_sec_crm_back.patrols.repositories.projections;

import java.time.Instant;

public interface PatrolReportDetailsAggregation {
    Long getLocationId();
    String getLocationName();
    Long getTaskId();
    String getTaskName();
    Instant getTaskStartDateTime();
    Instant getTaskEndDateTime();
    String getStatus();
    Long getSiteId();
    String getSiteName();
    Long getServiceId();
    String getServiceName();

    Boolean getHasEvidence();
    String getEvidenceImage();

    String getComment();
    Boolean getCommentCheck();
}
