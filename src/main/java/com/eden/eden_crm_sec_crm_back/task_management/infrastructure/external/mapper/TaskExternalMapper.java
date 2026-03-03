package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.mapper;

import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.SubmitTaskCheckExecutionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckDefinitionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionSummaryResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionSummaryPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckComparisonPayload;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskExternalMapper {

    // ── Response → Payload (output) ──────────────────────────────────────────

    TaskDefinitionPayload toPayload(TaskDefinitionResponse response);

    TaskCheckDefinitionPayload toPayload(TaskCheckDefinitionResponse response);

    TaskDefinitionSummaryPayload toPayload(TaskDefinitionSummaryResponse response);

    TaskExecutionPayload toPayload(TaskExecutionResponse response);

    TaskCheckExecutionPayload toPayload(TaskCheckExecutionResponse response);

    List<TaskDefinitionPayload> toTaskDefinitionPayloadList(List<TaskDefinitionResponse> responses);

    List<TaskDefinitionSummaryPayload> toTaskDefinitionSummaryPayloadList(List<TaskDefinitionSummaryResponse> responses);

    // ── Payload → Request (input) ─────────────────────────────────────────────

    CreateTaskExecutionRequest toRequest(CreateTaskExecutionPayload payload);

    SubmitTaskCheckExecutionRequest toRequest(SubmitTaskCheckExecutionPayload payload);

    TaskCheckComparisonPayload toPayload(TaskCheckComparisonResponse response);
}
