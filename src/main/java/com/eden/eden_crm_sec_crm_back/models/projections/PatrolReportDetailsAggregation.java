package com.eden.eden_crm_sec_crm_back.models.projections;

import java.time.LocalDate;

public interface PatrolReportDetailsAggregation {
    Long getLocationId();
    Long getTaskId();
    String getTaskName();
    LocalDate getTaskStartDate();
    LocalDate getTaskEndDate();
    String getStatus();
    Long getSiteId();
    String getSiteName();
    Long getServiceId();
    String getServiceName();

    Boolean getHasEvidence();
    String getEvidenceImage();
}
