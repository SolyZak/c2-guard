package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.ImageComparisonFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckComparison;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckComparisonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageComparisonService {

    private final ImageComparisonFeignClient imageComparisonFeignClient;
    private final TaskCheckComparisonRepository taskCheckComparisonRepository;

    /**
     * Calls the external AI image comparison service asynchronously.
     * On success, updates the comparison record with the returned ratio.
     * On failure, logs the error and does NOT disrupt the main flow.
     */
    @Async
    @Transactional
    public void compareImagesAsync(ImageComparisonRequest request) {
        try {
            log.info("Calling image comparison service for comparisonId={}, checkExecutionId={}, refImageId={}",
                    request.getComparisonId(),
                    request.getTaskCheckExecutionId(),
                    request.getTaskLocationChecksImageId());

            ImageComparisonResponse response = imageComparisonFeignClient.requestComparison(request);

            if (response != null && response.getRatio() != null) {
                Optional<TaskCheckComparison> comparisonOpt =
                        taskCheckComparisonRepository.findById(request.getComparisonId());

                if (comparisonOpt.isPresent()) {
                    TaskCheckComparison comparison = comparisonOpt.get();
                    comparison.updateRatio(response.getRatio());
                    taskCheckComparisonRepository.save(comparison);
                    log.info("Updated comparison {} with ratio {}",
                            request.getComparisonId(), response.getRatio());
                } else {
                    log.warn("Comparison record {} not found when trying to update ratio",
                            request.getComparisonId());
                }
            } else {
                log.warn("Image comparison service returned null or empty ratio for comparisonId={}",
                        request.getComparisonId());
            }

        } catch (Exception e) {
            log.error("Failed to call image comparison service for comparisonId={}, checkExecutionId={}: {}",
                    request.getComparisonId(),
                    request.getTaskCheckExecutionId(),
                    e.getMessage(), e);
            // Swallow the exception — main execution flow is not affected
        }
    }
}