package com.eden.eden_crm_sec_crm_back.dynamicscheduler.tasks;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.DateTimeScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.mappers.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Validator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class TaskMissedStatusJob extends DateTimeScheduledTaskFactory {

    public static final String TASK_TYPE = "TaskMissedStatus";

    public TaskMissedStatusJob(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository,
        ScheduledTaskMapper scheduledTaskMapper,
        Validator validator
    ) {
        super(applicationContext, objectMapper, taskRepository, logRepository, scheduledTaskMapper, validator);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        ObjectNode result = createObjectNode();
        result.set("success", arguments);
        return result;
    }
}
