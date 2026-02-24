package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;

public interface TaskExecutionPresenter {

    TaskExecutionPayload createTaskExecution(CreateTaskExecutionPayload payload);

    TaskCheckExecutionPayload submitTaskCheckExecution(SubmitTaskCheckExecutionPayload payload);
}
