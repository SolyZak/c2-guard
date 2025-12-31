package com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface ScheduledTaskFactory {
    String getTaskType();

    AbstractScheduledTask create(
        ObjectMapper mapper,
        ScheduledTaskEntity entity,
        ScheduledTaskRepository taskRepo,
        ScheduledTaskExecutionLogRepository logRepo
    );
}
