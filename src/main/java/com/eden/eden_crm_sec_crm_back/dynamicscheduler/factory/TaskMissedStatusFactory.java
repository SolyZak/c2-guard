package com.eden.eden_crm_sec_crm_back.dynamicscheduler.factory;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.AbstractScheduledTask;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.ScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.task.TaskMissedStatusJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskMissedStatusFactory implements ScheduledTaskFactory {

    @Override
    public String getTaskType() {
        return "TaskMissedStatus";
    }

    @Override
    public AbstractScheduledTask create(
        ObjectMapper mapper,
        ScheduledTaskEntity entity,
        ScheduledTaskRepository taskRepo,
        ScheduledTaskExecutionLogRepository logRepo
    ) {
        return new TaskMissedStatusJob(mapper, entity, taskRepo, logRepo);
    }

}
