package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskCheckComparisonPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckComparisonPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import org.springframework.web.multipart.MultipartFile;

public interface TaskExecutionPresenter {

    TaskExecutionPayload createTaskExecution(CreateTaskExecutionPayload payload);

    TaskCheckExecutionPayload submitTaskCheckExecution(SubmitTaskCheckExecutionPayload payload);

    TaskCheckComparisonPayload createTaskCheckComparison(CreateTaskCheckComparisonPayload payload);

    String uploadCheckExecutionImage(Long checkDefId, MultipartFile image);

}