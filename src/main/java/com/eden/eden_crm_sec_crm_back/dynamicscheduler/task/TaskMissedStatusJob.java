package com.eden.eden_crm_sec_crm_back.dynamicscheduler.task;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.AbstractScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class TaskMissedStatusJob extends AbstractScheduledTaskFactory {

    public TaskMissedStatusJob(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository
    ) {
        super(applicationContext, objectMapper, taskRepository, logRepository);
    }

    @Override
    public String getTaskType() {
        return "TaskMissedStatus";
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        ObjectNode result = createObjectNode();
        result.set("success", arguments);
        return result;
    }
}
