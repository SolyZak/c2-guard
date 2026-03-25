package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.Impl;

import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskCheckComparisonRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.SubmitTaskCheckExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskCheckComparisonService;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskExecutionService;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.mapper.TaskExternalMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskCheckComparisonPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckComparisonPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TaskExecutionPresenterImpl implements TaskExecutionPresenter {

    private final TaskExecutionService taskExecutionService;
    private final TaskExternalMapper taskExternalMapper;
    private final TaskCheckComparisonService taskCheckComparisonService;

    @Override
    public TaskExecutionPayload createTaskExecution(CreateTaskExecutionPayload payload) {
        CreateTaskExecutionRequest request = new CreateTaskExecutionRequest();
        request.setWorkforceId(payload.getWorkforceId());
        request.setCustomerId(payload.getCustomerId());
        return taskExternalMapper.toPayload(
                taskExecutionService.createTaskExecution(request));
    }

    @Override
    public TaskCheckExecutionPayload submitTaskCheckExecution(SubmitTaskCheckExecutionPayload payload) {
        SubmitTaskCheckExecutionRequest request = new SubmitTaskCheckExecutionRequest();
        request.setTaskCheckDefinitionId(payload.getTaskCheckDefinitionId());
        request.setTaskExecutionId(payload.getTaskExecutionId());
        request.setCheckType(payload.getCheckType());
        request.setCheckValues(payload.getCheckValues());
        request.setEvidenceImagePath(payload.getEvidenceImagePath());
        request.setComment(payload.getComment());
        request.setCustomerId(payload.getCustomerId());
        return taskExternalMapper.toPayload(
                taskExecutionService.submitTaskCheckExecution(request));
    }

    @Override
    public TaskCheckComparisonPayload createTaskCheckComparison(CreateTaskCheckComparisonPayload payload) {
        CreateTaskCheckComparisonRequest request = new CreateTaskCheckComparisonRequest();
        request.setTaskCheckDefinitionId(payload.getTaskCheckDefinitionId());
        request.setTaskCheckExecutionId(payload.getTaskCheckExecutionId());
        request.setCustomerId(payload.getCustomerId());
        request.setLocationId(payload.getLocationId());
        request.setEvidenceImagePath(payload.getEvidenceImagePath());
        request.setMissingQuality(payload.getMissingQuality());
        return taskExternalMapper.toPayload(
                taskExecutionService.createTaskCheckComparison(request));
    }

    @Override
    public String uploadCheckExecutionImage(Long checkDefId, MultipartFile image) {
        return taskCheckComparisonService.uploadExecutionImage(checkDefId, image);
    }
}