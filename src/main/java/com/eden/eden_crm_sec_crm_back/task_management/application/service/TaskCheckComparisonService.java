package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Transactional(readOnly = true)
    public List<TaskCheckComparisonReportResponse> getComparisonReport(LocalDate from, LocalDate to) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);

        List<TaskCheckComparisonReportProjection> rows =
                taskCheckComparisonRepository.findComparisonReport(customerId, fromDate, toDate);

        if (rows.isEmpty()) {
            return List.of();
        }

        String storageBaseUrl = oracleStorageUtil.getStorageUrl();

        Map<Long, String> workforceNames = resolveWorkforceNames(rows);

        return rows.stream()
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

        return taskMapper.toTaskCheckComparisonResponse(comparison);
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
}