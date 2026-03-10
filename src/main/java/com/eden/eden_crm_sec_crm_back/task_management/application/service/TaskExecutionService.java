package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskCheckComparisonRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.SubmitTaskCheckExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.mapper.TaskMapper;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckComparison;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskLocationChecksImage;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckComparisonRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskLocationChecksImageRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.service.TaskExecutionDomainService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.CheckType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecutionService {

    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskCheckExecutionRepository taskCheckExecutionRepository;
    private final TaskCheckComparisonRepository taskCheckComparisonRepository;
    private final TaskLocationChecksImageRepository taskLocationChecksImageRepository;
    private final TaskExecutionDomainService taskExecutionDomainService;
    private final TaskMapper taskMapper;
    private final ImageComparisonService imageComparisonService;

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

    @Transactional
    public TaskCheckComparisonResponse createTaskCheckComparison(CreateTaskCheckComparisonRequest request) {

        // ── Resolve the reference image ──────────────────────────────────────
        Long taskLocationChecksImageId = null;
        String referenceImagePath = null;

        if (request.getLocationId() != null) {
            Optional<TaskLocationChecksImage> refImageOpt =
                    taskLocationChecksImageRepository.findByLocationIdAndTaskCheckDefinitionId(
                            request.getLocationId(), request.getTaskCheckDefinitionId());

            if (refImageOpt.isPresent()) {
                TaskLocationChecksImage refImage = refImageOpt.get();
                taskLocationChecksImageId = refImage.getId();
                referenceImagePath = refImage.getRefImage();
            }
        }

        // ── Create and persist the comparison ────────────────────────────────
        TaskCheckComparison comparison = TaskCheckComparison.create(
                request.getTaskCheckDefinitionId(),
                request.getTaskCheckExecutionId(),
                null,
                null,
                request.getCustomerId(),
                taskLocationChecksImageId);

        comparison = taskCheckComparisonRepository.save(comparison);

        // ── Fire async AI comparison (non-blocking, failure-safe) ────────────
        if (taskLocationChecksImageId != null
                && referenceImagePath != null
                && request.getEvidenceImagePath() != null) {

            imageComparisonService.compareImagesAsync(
                    ImageComparisonRequest.builder()
                            .comparisonId(comparison.getId())
                            .taskCheckExecutionId(request.getTaskCheckExecutionId())
                            .taskLocationChecksImageId(taskLocationChecksImageId)
                            .referenceImagePath(referenceImagePath)
                            .evidenceImagePath(request.getEvidenceImagePath())
                            .customerId(request.getCustomerId())
                            .build());
        }

        return taskMapper.toTaskCheckComparisonResponse(comparison);
    }
}