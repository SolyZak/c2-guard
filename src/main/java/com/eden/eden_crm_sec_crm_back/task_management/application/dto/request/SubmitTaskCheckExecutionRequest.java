package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SubmitTaskCheckExecutionRequest {

    @NotNull
    private Long taskCheckDefinitionId;

    @NotNull
    private Long taskExecutionId;

    @NotNull
    private String checkType;

    @NotNull
    private TaskCheckValue checkValues;

    private String evidenceImagePath;
    private String comment;

    @NotNull
    private Long customerId;

}
