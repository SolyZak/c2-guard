package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.base;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.CreateScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;

import java.util.List;

public interface ScheduledTaskCreationFactory<T extends CreateScheduledTaskRequest> {
    ScheduledTaskEntity createTask(T request);
    List<ScheduledTaskEntity> createTasks(List<T> requests);
}
