package com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;

public interface ScheduledTaskFactory {
    String getTaskType();

    AbstractScheduledTaskFactory createInstanceAndSetEntity(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository,
        ScheduledTaskEntity taskEntity
    );

    JsonNode performTask(JsonNode arguments);
}
