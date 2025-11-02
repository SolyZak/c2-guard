package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDTO;

import java.util.List;

public record TaskCheckDto(
        String taskName,
        List<TaskCheckDTO> checks
) {
}
