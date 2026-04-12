package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskCheckComparisonRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.SubmitTaskCheckExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionCountResponse;
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
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final OracleStorageUtil oracleStorageUtil;

    // ======================== NEW: Task Execution Stats ========================

    /**
     * Counts the number of task executions performed by the given workforce for today.
     */
    @Transactional(readOnly = true)
    public long countPerformedTasksToday(Long workforceId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = today.plusDays(1).atStartOfDay();
        return taskExecutionRepository.countByWorkforceIdAndCreatedAtBetween(
                workforceId, startOfDay, startOfNextDay
        );
    }

    /**
     * Returns the performed tasks count for today wrapped in a response DTO.
     */
    @Transactional(readOnly = true)
    public TaskExecutionCountResponse getPerformedTasksCountToday(Long workforceId) {
        long count = countPerformedTasksToday(workforceId);
        return TaskExecutionCountResponse.builder()
                .performedTasksToday(count)
                .build();
    }

    // ======================== EXISTING METHODS (unchanged) ========================

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

        TaskCheckComparison comparison = TaskCheckComparison.create(
                request.getTaskCheckDefinitionId(),
                request.getTaskCheckExecutionId(),
                null,
                null,
                request.getCustomerId(),
                taskLocationChecksImageId,
                request.getMissingQuality());

        comparison = taskCheckComparisonRepository.save(comparison);

        // Dispatch the async image comparison AFTER the transaction commits
        // so the comparison record is visible to the async thread's transaction.
        if (taskLocationChecksImageId != null
                && referenceImagePath != null
                && request.getEvidenceImagePath() != null) {

            String storageBaseUrl = oracleStorageUtil.getStorageUrl();

            ImageComparisonRequest comparisonRequest = ImageComparisonRequest.builder()
                    .taskCheckExecutionId(request.getTaskCheckExecutionId())
                    .taskLocationChecksImageId(taskLocationChecksImageId)
                    .referenceImagePath(storageBaseUrl + referenceImagePath)
                    .evidenceImagePath(storageBaseUrl + request.getEvidenceImagePath())
                    .build();

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            imageComparisonService.compareImagesAsync(comparisonRequest);
                        } catch (Exception e) {
                            log.warn("Could not dispatch async image comparison for checkExecutionId={}: {}",
                                    comparisonRequest.getTaskCheckExecutionId(), e.getMessage());
                        }
                    }
                });
            } else {
                // Fallback: no active transaction synchronization — fire directly
                log.warn("No active transaction synchronization — dispatching image comparison directly "
                        + "for checkExecutionId={}", request.getTaskCheckExecutionId());
                try {
                    imageComparisonService.compareImagesAsync(comparisonRequest);
                } catch (Exception e) {
                    log.warn("Could not dispatch async image comparison for checkExecutionId={}: {}",
                            request.getTaskCheckExecutionId(), e.getMessage());
                }
            }
        }

        return taskMapper.toTaskCheckComparisonResponse(comparison);
    }
}