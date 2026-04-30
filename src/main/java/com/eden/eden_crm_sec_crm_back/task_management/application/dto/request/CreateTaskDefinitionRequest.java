package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateTaskDefinitionRequest {

    @NotBlank
    private String name;

    @NotNull
    private String severity;

    @Valid
    private List<CreateTaskCheckDefinitionRequest> checks;

}
