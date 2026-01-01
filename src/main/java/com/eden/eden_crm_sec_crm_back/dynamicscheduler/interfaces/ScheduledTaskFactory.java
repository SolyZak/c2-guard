package com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface ScheduledTaskFactory {
    String getTaskType();

    AbstractScheduledTaskFactory createInstance(
        ObjectMapper objectMapper,
        ScheduledTaskEntity taskEntity,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository
    );

    JsonNode performTask(JsonNode arguments);
}
