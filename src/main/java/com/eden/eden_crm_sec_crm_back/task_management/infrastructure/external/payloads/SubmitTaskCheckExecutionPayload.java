package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitTaskCheckExecutionPayload {
    private Long taskCheckDefinitionId;
    private Long taskExecutionId;
    private String checkType;
    private TaskCheckValue checkValues;
    private String evidenceImagePath;
    private String comment;
    private Long customerId;
}
