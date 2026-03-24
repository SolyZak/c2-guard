package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.Impl;

import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskDefinitionService;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskLocationChecksImageService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskLocationChecksImage;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskLocationChecksImageRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.mapper.TaskExternalMapper;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionSummaryPayload;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskPresenterImpl implements TaskPresenter {

    private final TaskDefinitionService taskDefinitionService;
    private final TaskLocationChecksImageService taskLocationChecksImageService;
    private final TaskLocationChecksImageRepository taskLocationChecksImageRepository;
    private final TaskExternalMapper taskExternalMapper;
    private final OracleStorageUtil oracleStorageUtil;

    @Override
    public TaskDefinitionPayload getTaskDefinition(Long id) {
        return taskExternalMapper.toPayload(taskDefinitionService.getTaskDefinition(id));
    }

    @Override
    public TaskDefinitionPayload getTaskDefinitionWithReferenceImages(Long id, Long locationId) {
        TaskDefinitionPayload payload = taskExternalMapper.toPayload(
                taskDefinitionService.getTaskDefinition(id));

        // Fetch all reference images for this task + location in ONE query
        List<TaskLocationChecksImage> images =
                taskLocationChecksImageRepository.findAllByLocationIdAndTaskDefinitionId(locationId, id);

        // Build a map: taskCheckDefinitionId → refImage (full URL)
        String storageBaseUrl = oracleStorageUtil.getStorageUrl();
        Map<Long, String> checkIdToImageUrl = images.stream()
                .filter(img -> img.getRefImage() != null && !img.getRefImage().isBlank())
                .collect(Collectors.toMap(
                        TaskLocationChecksImage::getTaskCheckDefinitionId,
                        img -> storageBaseUrl + img.getRefImage(),
                        (a, b) -> a  // in case of duplicates, keep first
                ));

        // Enrich each check payload with its reference image URL
        if (payload.getChecks() != null) {
            for (TaskCheckDefinitionPayload check : payload.getChecks()) {
                check.setReferenceImageUrl(checkIdToImageUrl.get(check.getId()));
            }
        }

        return payload;
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