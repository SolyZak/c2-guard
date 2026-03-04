package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionSummaryPayload;

import java.util.List;

public interface TaskPresenter {

    TaskDefinitionPayload getTaskDefinition(Long id);

    List<TaskDefinitionPayload> listTaskDefinitions(int page, int size);

    List<TaskDefinitionSummaryPayload> listAllTaskDefinitions();

    /**
     * Creates task_location_checks_image rows for every check definition
     * belonging to the given task definition, for the given location.
     * Skips if rows already exist for a location + check definition combination.
     */
    void initLocationCheckImages(Long taskDefinitionId, Long locationId, Long customerId);
}