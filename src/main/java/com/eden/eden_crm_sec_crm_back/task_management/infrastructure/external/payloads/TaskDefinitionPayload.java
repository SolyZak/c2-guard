package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaskDefinitionPayload {
    private Long id;
    private String name;
    private String severity;
    private List<TaskCheckDefinitionPayload> checks;
}
