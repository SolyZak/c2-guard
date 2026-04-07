package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmittedTextCheckDTO extends SubmittedCheckDTO {

    @NotNull(message = "{validation.distribution.task-check.notes}")
    private String notes;
}