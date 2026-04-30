package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskCheckDefinitionPayload {
    private Long id;
    private String name;
    private String severity;
    private String checkType;
    private TaskCheckValue checkSettings;
    private boolean hasEvidence;
    private boolean hasComment;
    private String referenceImageUrl;
}
