package com.eden.eden_crm_sec_crm_back.task_management.application.mapper;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskLocationChecksImageRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckComparisonResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckDefinitionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionSummaryResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskExecutionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskLocationChecksImageResponse;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckComparison;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskExecution;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskLocationChecksImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "severity", source = "severity")
    @Mapping(target = "checks", source = "checks")
    TaskDefinitionResponse toTaskDefinitionResponse(TaskDefinition task);

    @Mapping(target = "severity", source = "severity")
    @Mapping(target = "checkType", source = "checkType")
    TaskCheckDefinitionResponse toTaskCheckDefinitionResponse(TaskCheckDefinition check);

    TaskDefinitionSummaryResponse toTaskDefinitionSummaryResponse(TaskDefinition task);

    TaskExecutionResponse toTaskExecutionResponse(TaskExecution execution);

    @Mapping(target = "checkType", source = "checkType")
    TaskCheckExecutionResponse toTaskCheckExecutionResponse(TaskCheckExecution checkExecution);

    TaskCheckComparisonResponse toTaskCheckComparisonResponse(TaskCheckComparison comparison);

    List<TaskDefinitionResponse> toTaskDefinitionResponseList(List<TaskDefinition> tasks);

    List<TaskDefinitionSummaryResponse> toTaskDefinitionSummaryResponseList(List<TaskDefinition> tasks);


    // ---- TaskLocationChecksImage ----

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    TaskLocationChecksImage toDomain(CreateTaskLocationChecksImageRequest request);

    TaskLocationChecksImageResponse toResponse(TaskLocationChecksImage domain);
}