package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.Impl;

import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskExecutionService;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.mapper.TaskExternalMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskExecutionPresenterImpl implements TaskExecutionPresenter {

    private final TaskExecutionService taskExecutionService;
    private final TaskExternalMapper taskExternalMapper;

    @Override
    public TaskExecutionPayload createTaskExecution(CreateTaskExecutionPayload payload) {
        return taskExternalMapper.toPayload(
                taskExecutionService.createTaskExecution(taskExternalMapper.toRequest(payload)));
    }

    @Override
    public TaskCheckExecutionPayload submitTaskCheckExecution(SubmitTaskCheckExecutionPayload payload) {
        return taskExternalMapper.toPayload(
                taskExecutionService.submitTaskCheckExecution(taskExternalMapper.toRequest(payload)));
    }
}
