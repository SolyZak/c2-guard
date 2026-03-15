package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.DocumentsFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.ReferenceImageUploadedRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.UploadImageRequest;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskLocationChecksImageRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckImageResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskLocationChecksImageResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.mapper.TaskMapper;
import com.eden.eden_crm_sec_crm_back.task_management.domain.exception.TaskDomainException;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskLocationChecksImage;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckDefinitionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskLocationChecksImageRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskLocationChecksImageJpaRepository;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskLocationChecksImageService {

    private final TaskLocationChecksImageRepository repository;
    private final TaskLocationChecksImageJpaRepository jpaRepository;
    private final TaskCheckDefinitionRepository taskCheckDefinitionRepository;
    private final TaskMapper mapper;
    private final Utils utils;
    private final DocumentsFeignClient documentsFeignClient;
    private final OracleStorageUtil oracleStorageUtil;
    private final ImageComparisonService imageComparisonService;

    @Transactional
    public TaskLocationChecksImageResponse create(CreateTaskLocationChecksImageRequest request) {

        boolean exists = jpaRepository.existsByLocationIdAndTaskCheckDefinitionIdAndDeletedFalse(
                request.getLocationId(), request.getTaskCheckDefinitionId());

        if (exists) {
            throw new TaskDomainException(
                    "A reference image already exists for location ID: " + request.getLocationId()
                            + " and task check definition ID: " + request.getTaskCheckDefinitionId());
        }

        TaskLocationChecksImage domain = mapper.toDomain(request);
        TaskLocationChecksImage saved = repository.save(domain);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void initLocationCheckImages(Long taskDefinitionId, Long locationId, Long customerId) {
        List<TaskCheckDefinition> checkDefinitions =
                taskCheckDefinitionRepository.findAllByTaskDefinitionId(taskDefinitionId);

        if (checkDefinitions.isEmpty()) {
            log.debug("No check definitions found for task definition ID: {}. Skipping image init.",
                    taskDefinitionId);
            return;
        }

        for (TaskCheckDefinition check : checkDefinitions) {
            boolean exists = jpaRepository.existsByLocationIdAndTaskCheckDefinitionIdAndDeletedFalse(
                    locationId, check.getId());

            if (exists) {
                log.debug("Skipping: row already exists for location ID: {} and check definition ID: {}",
                        locationId, check.getId());
                continue;
            }

            TaskLocationChecksImage image = TaskLocationChecksImage.builder()
                    .taskDefinitionId(taskDefinitionId)
                    .locationId(locationId)
                    .taskCheckDefinitionId(check.getId())
                    .customerId(customerId)
                    .build();

            repository.save(image);
            log.debug("Created task_location_checks_image for location ID: {}, check definition ID: {}",
                    locationId, check.getId());
        }
    }

    @Transactional
    public TaskCheckImageResponse uploadTaskCheckImage(Long taskCheckDefinitionId, Long locationId, MultipartFile image) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        TaskCheckDefinition check = taskCheckDefinitionRepository.findById(taskCheckDefinitionId)
                .orElseThrow(() -> new BusinessException("task-check-not-found", HttpStatus.NOT_FOUND));

        if (!check.getCustomerId().equals(customerId)) {
            throw new BusinessException("task-check-not-found", HttpStatus.NOT_FOUND);
        }

        TaskLocationChecksImage locationChecksImage = repository
                .findByLocationIdAndTaskCheckDefinitionId(locationId, taskCheckDefinitionId)
                .orElseThrow(() -> new BusinessException("task-location-check-image-not-found", HttpStatus.NOT_FOUND));

        String imagePath = generateImagePath(
                locationChecksImage.getId(),
                taskCheckDefinitionId,
                locationId,
                image
        );

        try {
            documentsFeignClient.uploadImage(
                    UploadImageRequest.builder()
                            .image(image)
                            .path(imagePath)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error uploading task check image for check id {} and location id {}: {}",
                    taskCheckDefinitionId, locationId, e.getMessage());
            throw new BusinessException("task-check-image-upload-failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            repository.updateRefImage(locationId, taskCheckDefinitionId, imagePath);
        } catch (Exception e) {
            log.error("Error saving image URL in DB for check id {} and location id {}: {}",
                    taskCheckDefinitionId, locationId, e.getMessage());
            throw new BusinessException("task-check-image-save-failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TaskLocationChecksImage updated = repository
                .findByLocationIdAndTaskCheckDefinitionId(locationId, taskCheckDefinitionId)
                .orElseThrow(() -> new BusinessException("task-location-check-image-not-found", HttpStatus.NOT_FOUND));

        if (updated.getRefImage() == null || updated.getRefImage().isBlank()) {
            log.error("Image URL not persisted in DB for check id {} and location id {}",
                    taskCheckDefinitionId, locationId);
            throw new BusinessException("task-check-image-save-failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String fullUrl = oracleStorageUtil.getStorageUrl() + imagePath;

        // ── Fire async notification to AI service (non-blocking, failure-safe) ───
        imageComparisonService.notifyReferenceImageUploadedAsync(
                ReferenceImageUploadedRequest.builder()
                        .taskLocationChecksImageId(updated.getId())
                        .referenceImageUrl(fullUrl)
                        .build());

        return new TaskCheckImageResponse(taskCheckDefinitionId, locationId, fullUrl);
    }

    private String generateImagePath(Long refImageId, Long taskCheckDefinitionId, Long locationId, MultipartFile file) {
        String fileExtension = extractFileExtension(file);
        return String.format("%d/taskcheck/%d/tasklocation/%d/image%s",
                refImageId,
                taskCheckDefinitionId,
                locationId,
                fileExtension
        );
    }

    private String extractFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return "";
    }
}