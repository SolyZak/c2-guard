package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskExecutionCountResponse {
    private long performedTasksToday;
}