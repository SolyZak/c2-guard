package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.WorkforceLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteJobDescResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.DecimalCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.ListCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.NumberCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TextCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.WorkforceTaskCheckPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.WorkforceTaskPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workforce")
@Tag(
        name = "Workforce App APIs",
        description = "This part is for workforce app part of the system "
                + "it'll provide the needed APIs for workforce landing screen"
)
@RequiredArgsConstructor
public class WorkforceController {

    private final WorkforceService workforceService;
    private final CustomerSiteService customerSiteService;
    private final TaskPresenter taskPresenter;

    @Operation(summary = "Get customers dropdown list, that's workforce "
            + "security company contracted with")
    @GetMapping("/customers/dropdown")
    public ApiResponse<List<GeneralDropdown>> customersDropdown() {
        return ApiResponse.ok(workforceService.customersDropdown());
    }

    @Operation(summary = "Get contracts dropdown list related to customer, "
            + "that's workforce security company contracted with")
    @GetMapping("/customers/{id}/contracts")
    public ApiResponse<List<GeneralDropdown>> customersContractDropdown(
            @PathVariable("id") Long customerId) {
        return ApiResponse.ok(
                workforceService.customersContractDropdown(customerId));
    }

    @Operation(summary = "Get operations sites dropdown list related to "
            + "customer & contract")
    @GetMapping("/customers/{id}/contracts/{contractId}/operation-sites")
    public ApiResponse<List<WorkforceSiteDistributionDto>>
    customersContractOperationSitesDropdown(
            @PathVariable("id") Long customerId,
            @PathVariable("contractId") Long contractId) {
        return ApiResponse.ok(workforceService
                .customersContractOperationSitesDropdown(
                        customerId, contractId));
    }

    @Operation(summary = "Get operations sites dropdown list")
    @GetMapping("/operation-sites/dropdown")
    public ApiResponse<List<GeneralDropdown>> operationSitesDropdown() {
        return ApiResponse.ok(workforceService.operationSitesDropdown());
    }

    @Operation(summary = "Get operation site services dropdown list")
    @GetMapping("/operation-sites/{id}/services")
    public ApiResponse<WorkforceSiteDistributionDto>
    operationSiteServicesDropdown(
            @PathVariable("id") Long id,
            @RequestParam(value = "contractId", required = false)
            Long contractId) {
        return ApiResponse.ok(
                workforceService.operationSiteServicesDropdown(
                        id, contractId));
    }

    @Operation(summary = "Get operation site job desc By Id")
    @GetMapping("/job-desc/{id}")
    public ApiResponse<CustomerSiteJobDescResponseDto>
    getCustomerSiteJobDesc(@PathVariable Long id) {
        return ApiResponse.ok(
                customerSiteService
                        .getCustomerSiteJobDescriptionById(id));
    }

    @Operation(summary = "Get task by ID with checks")
    @GetMapping("/{taskId}")
    public ApiResponse<WorkforceTaskPayload> getTaskById(
            @PathVariable("taskId") Long taskId,
            @RequestParam(value = "locationId", required = false)
            Long locationId) {

        TaskDefinitionPayload taskDefinition;
        if (locationId != null) {
            taskDefinition = taskPresenter
                    .getTaskDefinitionWithReferenceImages(
                            taskId, locationId);
        } else {
            taskDefinition = taskPresenter
                    .getTaskDefinition(taskId);
        }
        return ApiResponse.ok(toWorkforcePayload(taskDefinition));
    }

    @PostMapping("/{id}/location")
    public ApiResponse<Map<String, String>> addWorkforceLocation(
            @PathVariable("id") Long workforceId,
            @Valid @RequestBody WorkforceLocationRequest request) {
        workforceService.addWorkforceLocation(
                workforceId, request);
        return ApiResponse.ok(Map.of("message",
                "Workforce location updated successfully"));
    }

    // ── Private: map new module payloads to old response shape ──

    private static WorkforceTaskPayload toWorkforcePayload(
            TaskDefinitionPayload taskDefinition) {
        List<WorkforceTaskCheckPayload> checks = taskDefinition
                .getChecks()
                .stream()
                .map(WorkforceController::toWorkforceCheckPayload)
                .toList();

        return WorkforceTaskPayload.builder()
                .name(taskDefinition.getName())
                .checks(checks)
                .build();
    }

    private static WorkforceTaskCheckPayload toWorkforceCheckPayload(
            TaskCheckDefinitionPayload checkDef) {

        var builder = WorkforceTaskCheckPayload.builder()
                .id(checkDef.getId())
                .name(checkDef.getName())
                .type(checkDef.getCheckType().toLowerCase())
                .evidence(checkDef.isHasEvidence())
                .commentCheck(checkDef.isHasComment())
                .referenceImageUrl(checkDef.getReferenceImageUrl());

        switch (checkDef.getCheckType()) {
            case "TEXT" -> {
                if (checkDef.getCheckSettings()
                        instanceof TextCheckValue v) {
                    builder.notes(v.getNotes());
                }
            }
            case "NUMBER" -> {
                if (checkDef.getCheckSettings()
                        instanceof NumberCheckValue v) {
                    builder.unit(v.getUnit());
                    builder.operator(v.getOperator());
                    builder.value(v.getValue());
                }
            }
            case "DECIMAL" -> {
                if (checkDef.getCheckSettings()
                        instanceof DecimalCheckValue v) {
                    builder.unit(v.getUnit());
                    builder.operator(v.getOperator());
                    builder.value(v.getValue());
                }
            }
            case "LIST" -> {
                if (checkDef.getCheckSettings()
                        instanceof ListCheckValue v) {
                    builder.listItems(v.getItems());
                }
            }
            default -> throw new IllegalStateException(
                    "Unknown check type: "
                            + checkDef.getCheckType());
        }

        return builder.build();
    }
}