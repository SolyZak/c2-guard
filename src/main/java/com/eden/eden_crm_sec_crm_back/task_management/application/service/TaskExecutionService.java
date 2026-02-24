package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.SubmitTaskCheckExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.mapper.TaskMapper;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.service.TaskExecutionDomainService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.CheckType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskExecutionService {

    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskCheckExecutionRepository taskCheckExecutionRepository;
    private final TaskExecutionDomainService taskExecutionDomainService;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskExecutionResponse createTaskExecution(CreateTaskExecutionRequest request) {
        TaskExecution execution = TaskExecution.create(request.getWorkforceId(), request.getCustomerId());
        execution = taskExecutionRepository.save(execution);
        return taskMapper.toTaskExecutionResponse(execution);
    }

    @Transactional
    public TaskCheckExecutionResponse submitTaskCheckExecution(SubmitTaskCheckExecutionRequest request) {
        taskExecutionDomainService.validateCheckValues(request.getCheckValues());
        TaskCheckExecution checkExecution = TaskCheckExecution.submit(
                request.getTaskCheckDefinitionId(),
                request.getTaskExecutionId(),
                CheckType.fromString(request.getCheckType()),
                request.getCheckValues(),
                request.getEvidenceImagePath(),
                request.getComment(),
                request.getCustomerId());
        checkExecution = taskCheckExecutionRepository.save(checkExecution);
        return taskMapper.toTaskCheckExecutionResponse(checkExecution);
    }
}
