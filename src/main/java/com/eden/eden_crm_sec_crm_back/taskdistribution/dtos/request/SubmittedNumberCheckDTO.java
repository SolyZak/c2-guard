package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmittedNumberCheckDTO extends SubmittedCheckDTO {

    private String unit;

    private String operator;

    @NotNull(message = "{validation.distribution.task-check.value}")
    private Integer value;
}