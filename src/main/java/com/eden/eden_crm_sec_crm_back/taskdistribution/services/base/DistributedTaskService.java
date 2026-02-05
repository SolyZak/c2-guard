package com.eden.eden_crm_sec_crm_back.taskdistribution.services.base;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ExecuteDistributedTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.TodayTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTasksResponse;

public interface DistributedTaskService {
    TodayTasksResponse getTodayTasks(TodayTasksRequest todayTasksRequest);
    void executeTask(ExecuteDistributedTaskRequest executeDistributedTaskRequest);
}
