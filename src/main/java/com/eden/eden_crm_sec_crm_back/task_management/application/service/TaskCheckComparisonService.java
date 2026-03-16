package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.DocumentsFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.MatchingFeedbackRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.UploadImageRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.UpdateTaskCheckComparisonMatchingRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonReportResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.mapper.TaskMapper;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckComparison;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckComparisonRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository.TaskCheckComparisonReportProjection;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCheckComparisonService {

    private final TaskCheckComparisonRepository taskCheckComparisonRepository;
    private final OracleStorageUtil oracleStorageUtil;
    private final OrgUnitClient orgUnitClient;
    private final TaskMapper taskMapper;
    private final Utils utils;
    private final DocumentsFeignClient documentsFeignClient;
    private final ImageComparisonService imageComparisonService;

    @Transactional(readOnly = true)
    public PaginateResponse<TaskCheckComparisonReportResponse> getComparisonReport(
            LocalDate from, LocalDate to, Long locationId, String taskName,
            int page, int size, String sortBy, String sortDirection) {

        Long customerId = utils.getLoggedInUser().getCustomerId();

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);

        String normalizedTaskName = (taskName != null && !taskName.isBlank()) ? taskName.trim() : null;

        Pageable pageable = PageRequest.of(page, size);

        Page<TaskCheckComparisonReportProjection> resultPage =
                taskCheckComparisonRepository.findComparisonReportPaginated(
                        customerId, fromDate, toDate, locationId, normalizedTaskName, pageable);

        if (resultPage.isEmpty()) {
            return new PaginateResponse<>(
                    List.of(),
                    page,
                    size,
                    0L,
                    0L
            );
        }

        String storageBaseUrl = oracleStorageUtil.getStorageUrl();
        Map<Long, String> workforceNames = resolveWorkforceNames(resultPage.getContent());

        List<TaskCheckComparisonReportResponse> content = resultPage.getContent().stream()
                .map(row -> TaskCheckComparisonReportResponse.builder()
                        .comparisonId(row.getComparisonId())
                        .comparisonDate(row.getComparisonDate())
                        .comparisonRatio(row.getComparisonRatio())
                        .matching(row.getMatching())
                        .taskDefinitionId(row.getTaskDefinitionId())
                        .taskDefinitionName(row.getTaskDefinitionName())
                        .checkDefinitionId(row.getCheckDefinitionId())
                        .checkDefinitionName(row.getCheckDefinitionName())
                        .checkBaseImage(resolveStorageUrl(storageBaseUrl, row.getCheckBaseImagePath()))
                        .checkExecutionId(row.getCheckExecutionId())
                        .checkTransactionImage(resolveTransactionImage(storageBaseUrl, row.getCheckTransactionImagePath()))
                        .workforceId(row.getWorkforceId())
                        .workforceName(workforceNames.get(row.getWorkforceId()))
                        .locationId(row.getLocationId())
                        .locationName(row.getLocationName())
                        .build())
                .toList();

        Comparator<TaskCheckComparisonReportResponse> comparator = getComparator(sortBy);
        if (sortDirection.equalsIgnoreCase("DESC")) {
            comparator = comparator.reversed();
        }
        content = content.stream().sorted(comparator).toList();

        return new PaginateResponse<>(
                content,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                (long) resultPage.getTotalPages()
        );
    }

    @Transactional
    public TaskCheckComparisonResponse updateMatching(Long id, UpdateTaskCheckComparisonMatchingRequest request) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        TaskCheckComparison comparison = taskCheckComparisonRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task-check-comparison-not-found", HttpStatus.NOT_FOUND));

        if (!comparison.getCustomerId().equals(customerId)) {
            throw new BusinessException("task-check-comparison-not-found", HttpStatus.NOT_FOUND);
        }

        comparison.updateMatching(request.getMatching());
        comparison = taskCheckComparisonRepository.save(comparison);

        try {
            imageComparisonService.sendMatchingFeedbackAsync(
                    MatchingFeedbackRequest.builder()
                            .taskCheckExecutionId(comparison.getTaskCheckExecutionId())
                            .taskLocationChecksImageId(comparison.getTaskLocationChecksImageId())
                            .matching(comparison.getMatching())
                            .build());
        } catch (Exception e) {
            log.warn("Could not dispatch async matching feedback for comparisonId={}: {}",
                    comparison.getTaskCheckExecutionId(), e.getMessage());
        }

        return taskMapper.toTaskCheckComparisonResponse(comparison);
    }

    private Comparator<TaskCheckComparisonReportResponse> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "comparisonratio" -> Comparator.comparing(
                    TaskCheckComparisonReportResponse::getComparisonRatio,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "taskdefinitionname" -> Comparator.comparing(
                    TaskCheckComparisonReportResponse::getTaskDefinitionName,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "checkdefinitionname" -> Comparator.comparing(
                    TaskCheckComparisonReportResponse::getCheckDefinitionName,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "locationname" -> Comparator.comparing(
                    TaskCheckComparisonReportResponse::getLocationName,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "matching" -> Comparator.comparing(
                    TaskCheckComparisonReportResponse::getMatching,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(
                    TaskCheckComparisonReportResponse::getComparisonDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }

    private Map<Long, String> resolveWorkforceNames(List<TaskCheckComparisonReportProjection> rows) {
        Map<Long, String> workforceNames = new HashMap<>();
        rows.stream()
                .map(TaskCheckComparisonReportProjection::getWorkforceId)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(workforceId -> {
                    try {
                        WorkforceFullDataDto workforce = orgUnitClient.getWorkforceDetails(workforceId.intValue());
                        workforceNames.put(workforceId, workforce.workforce().name());
                    } catch (Exception e) {
                        log.warn("Failed to resolve workforce name for id {}: {}", workforceId, e.getMessage());
                        workforceNames.put(workforceId, null);
                    }
                });
        return workforceNames;
    }

    private String resolveStorageUrl(String storageBaseUrl, String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        return storageBaseUrl + path;
    }

    private String resolveTransactionImage(String storageBaseUrl, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.startsWith("data:") || value.startsWith("base64,") || value.length() > 500) {
            return value;
        }
        return storageBaseUrl + value;
    }

    public String uploadExecutionImage(Long checkDefId, MultipartFile image) {
        String filename = image.getOriginalFilename();
        String ext = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf('.') + 1) : "jpg";
        String path = "task-execution-images/" + checkDefId + "/exec_" + checkDefId + "_"
                + System.currentTimeMillis() + "." + ext;
        try {
            documentsFeignClient.uploadImage(UploadImageRequest.builder().image(image).path(path).build());
        } catch (Exception e) {
            log.error("Failed to upload check execution image for checkDefId {}: {}", checkDefId, e.getMessage());
            throw new BusinessException("Failed to upload check image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return path;
    }
}