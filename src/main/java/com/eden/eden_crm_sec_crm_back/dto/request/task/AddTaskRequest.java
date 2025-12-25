package com.eden.eden_crm_sec_crm_back.dto.request.task;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class AddTaskRequest {
    private String taskName;

    @Valid
    List<TaskCheckDTO> checks;
}
