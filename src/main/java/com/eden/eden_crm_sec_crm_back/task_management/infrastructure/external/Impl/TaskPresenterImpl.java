package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.Impl;

import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskDefinitionService;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskLocationChecksImageService;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.mapper.TaskExternalMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionSummaryPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskPresenterImpl implements TaskPresenter {

    private final TaskDefinitionService taskDefinitionService;
    private final TaskLocationChecksImageService taskLocationChecksImageService;
    private final TaskExternalMapper taskExternalMapper;

    @Override
    public TaskDefinitionPayload getTaskDefinition(Long id) {
        return taskExternalMapper.toPayload(taskDefinitionService.getTaskDefinition(id));
    }

    @Override
    public List<TaskDefinitionPayload> listTaskDefinitions(int page, int size) {
        return taskExternalMapper.toTaskDefinitionPayloadList(
                taskDefinitionService.listTaskDefinitions(page, size));
    }

    @Override
    public List<TaskDefinitionSummaryPayload> listAllTaskDefinitions() {
        return taskExternalMapper.toTaskDefinitionSummaryPayloadList(
                taskDefinitionService.listAllTaskDefinitions());
    }

    @Override
    public void initLocationCheckImages(Long taskDefinitionId, Long locationId, Long customerId) {
        taskLocationChecksImageService.initLocationCheckImages(taskDefinitionId, locationId, customerId);
    }
}