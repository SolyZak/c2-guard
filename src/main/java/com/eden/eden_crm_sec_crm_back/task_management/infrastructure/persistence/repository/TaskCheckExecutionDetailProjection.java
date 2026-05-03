package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import java.time.LocalDateTime;

public interface TaskCheckExecutionDetailProjection {
    Long getId();
    String getCheckName();
    String getCheckValues();
    String getEvidenceImagePath();
    LocalDateTime getCreatedAt();
}
