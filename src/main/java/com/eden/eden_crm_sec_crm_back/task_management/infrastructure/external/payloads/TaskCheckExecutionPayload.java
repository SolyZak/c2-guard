package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskCheckExecutionPayload {
    private Long id;
    private Long taskCheckDefinitionId;
    private Long taskExecutionId;
    private String checkType;
    private TaskCheckValue checkValues;
    private String evidenceImagePath;
    private String comment;
    private LocalDateTime createdAt;
}
