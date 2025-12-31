package com.eden.eden_crm_sec_crm_back.dynamicscheduler.task;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.AbstractScheduledTask;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class TaskMissedStatusJob extends AbstractScheduledTask {

    public TaskMissedStatusJob(
            ObjectMapper mapper,
            ScheduledTaskEntity taskEntity,
            ScheduledTaskRepository taskRepo,
            ScheduledTaskExecutionLogRepository logRepo
    ) {
        super(mapper, taskEntity, taskRepo, logRepo);
    }

    @Override
    public JsonNode performTask(JsonNode arguments) throws Exception {
        return null;
    }
}
