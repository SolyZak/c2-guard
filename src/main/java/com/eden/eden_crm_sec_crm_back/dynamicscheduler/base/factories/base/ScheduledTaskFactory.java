package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.base;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.AbstractScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.mappers.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.context.ApplicationContext;

public interface ScheduledTaskFactory {
    String getTaskType();

    AbstractScheduledTaskFactory createInstanceAndSetEntity(ScheduledTaskFactory factory, ScheduledTaskEntity task);

    JsonNode performTask(JsonNode arguments);

    ApplicationContext getApplicationContext();
    ObjectMapper getObjectMapper();
    ScheduledTaskRepository getTaskRepository();
    ScheduledTaskExecutionLogRepository getLogRepository();
    ScheduledTaskMapper getScheduledTaskMapper();
    Validator getValidator();
}
