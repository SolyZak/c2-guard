package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.DocumentsFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.UploadImageRequest;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskCheckDefinitionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskDefinitionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.*;
import com.eden.eden_crm_sec_crm_back.task_management.application.mapper.TaskMapper;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckDefinitionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskDefinitionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.service.TaskDefinitionDomainService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.CheckType;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.Severity;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.LocationTaskCheckProjection;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskDefinitionService {

    private static final String TASK_CHECK_IMAGE_PATH = "task-management/checks/";

    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskCheckDefinitionRepository taskCheckDefinitionRepository;
    private final TaskDefinitionDomainService taskDefinitionDomainService;
    private final TaskMapper taskMapper;
    private final Utils utils;
    private final DocumentsFeignClient documentsFeignClient;
    private final OracleStorageUtil oracleStorageUtil;

    @Transactional
    public void createTaskDefinition(CreateTaskDefinitionRequest request) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        TaskDefinition taskDefinition = TaskDefinition.create(
                request.getName(),
                Severity.fromString(request.getSeverity()),
                customerId);
        taskDefinition = taskDefinitionRepository.save(taskDefinition);

        if (request.getChecks() != null && !request.getChecks().isEmpty()) {
            List<TaskCheckDefinition> checks = new ArrayList<>();
            for (CreateTaskCheckDefinitionRequest checkReq : request.getChecks()) {
                taskDefinitionDomainService.validateCheckDefinition(checkReq.getCheckSettings(), checkReq.getName());
                TaskCheckDefinition check = TaskCheckDefinition.create(
                        taskDefinition.getId(),
                        checkReq.getName(),
                        Severity.fromString(checkReq.getSeverity()),
                        CheckType.fromString(checkReq.getCheckType()),
                        checkReq.getCheckSettings(),
                        checkReq.isHasEvidence(),
                        checkReq.isHasComment(),
                        customerId);
                checks.add(taskCheckDefinitionRepository.save(check));
            }
            for (TaskCheckDefinition check : checks) {
                taskDefinition.addCheck(check);
            }
        }
    }

    public List<TaskDefinitionResponse> listTaskDefinitions(int page, int size) {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        List<TaskDefinition> tasks = taskDefinitionRepository.findAllByCustomerId(customerId, page, size);
        return taskMapper.toTaskDefinitionResponseList(tasks);
    }

    public long countTaskDefinitions() {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        return taskDefinitionRepository.countByCustomerId(customerId);
    }

    public List<TaskDefinitionSummaryResponse> listAllTaskDefinitions() {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        List<TaskDefinition> tasks = taskDefinitionRepository.findAllByCustomerId(customerId);
        return taskMapper.toTaskDefinitionSummaryResponseList(tasks);
    }

    public TaskDefinitionResponse getTaskDefinition(Long id) {
        TaskDefinition task = taskDefinitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task-not-found", HttpStatus.NOT_FOUND));
        return taskMapper.toTaskDefinitionResponse(task);
    }

    public List<LocationTaskDefinitionsResponse> getTaskChecksByPremise(Long premiseId) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        List<LocationTaskCheckProjection> rows =
                taskCheckDefinitionRepository.findAllChecksByPremiseAndCustomer(
                        premiseId, customerId, LocationTaskCheckProjection.class);

        String storageBaseUrl = oracleStorageUtil.getStorageUrl();

        return rows.stream()
                .collect(Collectors.groupingBy(
                        LocationTaskCheckProjection::getLocationId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(locationEntry -> {
                    List<LocationTaskCheckProjection> locationRows = locationEntry.getValue();
                    LocationTaskCheckProjection firstRow = locationRows.get(0);

                    List<TaskDefinitionWithChecksResponse> taskDefinitions = locationRows.stream()
                            .collect(Collectors.groupingBy(
                                    LocationTaskCheckProjection::getTaskDefinitionId,
                                    LinkedHashMap::new,
                                    Collectors.toList()
                            ))
                            .entrySet().stream()
                            .map(tdEntry -> {
                                List<LocationTaskCheckProjection> tdRows = tdEntry.getValue();
                                LocationTaskCheckProjection firstTd = tdRows.get(0);

                                List<TaskCheckDefinitionDetailResponse> checks = tdRows.stream()
                                        .map(row -> new TaskCheckDefinitionDetailResponse(
                                                row.getCheckId(),
                                                row.getCheckName(),
                                                row.getImageUrl() != null
                                                        ? storageBaseUrl + row.getImageUrl()
                                                        : null
                                        ))
                                        .toList();

                                return new TaskDefinitionWithChecksResponse(
                                        firstTd.getTaskDefinitionId(),
                                        firstTd.getTaskDefinitionName(),
                                        checks
                                );
                            })
                            .toList();

                    return new LocationTaskDefinitionsResponse(
                            firstRow.getLocationId(),
                            firstRow.getLocationName(),
                            taskDefinitions
                    );
                })
                .toList();
    }

    @Transactional
    public TaskCheckImageResponse uploadTaskCheckImage(Long taskCheckDefinitionId, MultipartFile image) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        TaskCheckDefinition check = taskCheckDefinitionRepository.findById(taskCheckDefinitionId)
                .orElseThrow(() -> new BusinessException("task-check-not-found", HttpStatus.NOT_FOUND));

        if (!check.getCustomerId().equals(customerId)) {
            throw new BusinessException("task-check-not-found", HttpStatus.NOT_FOUND);
        }

        String imagePath = generateImagePath(taskCheckDefinitionId, image);

        try {
            documentsFeignClient.uploadImage(
                    UploadImageRequest.builder()
                            .image(image)
                            .path(imagePath)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error uploading task check image for id {}: {}", taskCheckDefinitionId, e.getMessage());
            throw new BusinessException("task-check-image-upload-failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            taskCheckDefinitionRepository.updateImageUrl(taskCheckDefinitionId, imagePath);
        } catch (Exception e) {
            log.error("Error saving image URL in DB for check id {}: {}", taskCheckDefinitionId, e.getMessage());
            throw new BusinessException("task-check-image-save-failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TaskCheckDefinition updated = taskCheckDefinitionRepository.findById(taskCheckDefinitionId)
                .orElseThrow(() -> new BusinessException("task-check-not-found", HttpStatus.NOT_FOUND));

        if (updated.getImageUrl() == null || updated.getImageUrl().isBlank()) {
            log.error("Image URL not persisted in DB for check id {}", taskCheckDefinitionId);
            throw new BusinessException("task-check-image-save-failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String fullUrl = oracleStorageUtil.getStorageUrl() + imagePath;
        return new TaskCheckImageResponse(taskCheckDefinitionId, fullUrl);
    }

    private String generateImagePath(Long taskCheckDefinitionId, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String uniqueFilename = String.format("check_%d_%d%s",
                taskCheckDefinitionId   , System.currentTimeMillis(), fileExtension);
        return TASK_CHECK_IMAGE_PATH + taskCheckDefinitionId + "/" + uniqueFilename;
    }
}