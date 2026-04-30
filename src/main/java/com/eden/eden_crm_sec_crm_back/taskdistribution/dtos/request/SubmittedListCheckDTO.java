package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmittedListCheckDTO extends SubmittedCheckDTO {

    @NotNull(message = "{validation.distribution.task-check.list-items}")
    private List<String> listItems;
}