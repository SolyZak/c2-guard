package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkforceTaskPayload {

    @JsonProperty("taskName")
    private String name;

    private List<WorkforceTaskCheckPayload> checks;
}