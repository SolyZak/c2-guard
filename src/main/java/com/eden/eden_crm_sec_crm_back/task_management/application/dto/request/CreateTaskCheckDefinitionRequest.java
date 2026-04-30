package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateTaskCheckDefinitionRequest {

    @NotBlank
    private String name;

    @NotNull
    private String severity;

    @NotNull
    private String checkType;

    @NotNull
    private TaskCheckValue checkSettings;

    private boolean hasEvidence;
    private boolean hasComment;

}
