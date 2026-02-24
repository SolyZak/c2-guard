package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.WorkforceLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDecimalDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckListDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckNumberDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckTextDTO;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteJobDescResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
// ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
// ACL interface and payload types from task_management module.
// CLEANUP: keep all imports permanently after Phase E; remove TaskService fallback.
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.DecimalCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.ListCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.NumberCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TextCheckValue;
// ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
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
        description = "This part is for workforce app part of the system it'll provide the needed APIs for workforce landing screen"
)
@RequiredArgsConstructor
public class WorkforceController {

    private final WorkforceService workforceService;
    private final CustomerSiteService customerSiteService;
    // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
    // Legacy service — still used as fallback for old task IDs.
    // CLEANUP: remove after Phase E when all tasks are in task_management.
    private final TaskService taskService;
    // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // ACL presenter — tried first for every getTaskById call.
    // CLEANUP: this field stays permanently after Phase E.
    private final TaskPresenter taskPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    @Operation(summary = "Get customers dropdown list, that's workforce security company contracted with")
    @GetMapping("/customers/dropdown")
    public ApiResponse<List<GeneralDropdown>> customersDropdown() {
        return ApiResponse.ok(workforceService.customersDropdown());
    }

    @Operation(summary = "Get contracts dropdown list related to customer, that's workforce security company contracted with")
    @GetMapping("/customers/{id}/contracts")
    public ApiResponse<List<GeneralDropdown>> customersContractDropdown(@PathVariable("id") Long customerId) {
        return ApiResponse.ok(workforceService.customersContractDropdown(customerId));
    }

    @Operation(summary = "Get operations sites dropdown list related to customer & contract, that's workforce security company contracted with")
    @GetMapping("/customers/{id}/contracts/{contractId}/operation-sites")
    public ApiResponse<List<WorkforceSiteDistributionDto>> customersContractOperationSitesDropdown(
            @PathVariable("id") Long customerId,
            @PathVariable("contractId") Long contractId
    ) {
        return ApiResponse.ok(workforceService.customersContractOperationSitesDropdown(customerId, contractId));
    }

    @Operation(summary = "Get operations sites dropdown list")
    @GetMapping("/operation-sites/dropdown")
    public ApiResponse<List<GeneralDropdown>> operationSitesDropdown() {
        return ApiResponse.ok(workforceService.operationSitesDropdown());
    }

    @Operation(summary = "Get operation site services dropdown list")
    @GetMapping("/operation-sites/{id}/services")
    public ApiResponse<WorkforceSiteDistributionDto> operationSiteServicesDropdown(
            @PathVariable("id") Long id,
            @RequestParam(value = "contractId", required = false) Long contractId
    ) {
        return ApiResponse.ok(workforceService.operationSiteServicesDropdown(id, contractId));
    }

    @Operation(summary = "Get operation site job desc By Id")
    @GetMapping("/job-desc/{id}")
    ApiResponse<CustomerSiteJobDescResponseDto> getCustomerSiteJobDesc(@PathVariable Long id) {
        return ApiResponse.ok(customerSiteService.getCustomerSiteJobDescriptionById(id));
    }

    @GetMapping("/{taskId}")
    public ApiResponse<TaskCheckDto> getTaskById(@PathVariable("taskId") Long taskId) {
        // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
        // Try the new task_management module first.
        // If it succeeds, convert TaskDefinitionPayload → TaskCheckDto and return.
        // CLEANUP: after Phase E, remove the try-catch; keep only the new-path block.
        try {
            TaskDefinitionPayload taskDefinition = taskPresenter.getTaskDefinition(taskId);
            return ApiResponse.ok(toTaskCheckDto(taskDefinition));
        } catch (Exception ignored) {
            // Task not found in new module — fall through to legacy path.
        }
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────

        // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────
        // Fallback: old task table. Reached only when taskPresenter throws.
        // CLEANUP: delete this block after Phase E.
        return ApiResponse.ok(taskService.getTaskById(taskId));
        // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────
    }

    @PostMapping("/{id}/location")
    public ApiResponse<Map<String, String>> addWorkforceLocation(
        @PathVariable("id") Long workforceId,
        @Valid@RequestBody WorkforceLocationRequest workforceLocationRequest
    ) {
        workforceService.addWorkforceLocation(workforceId, workforceLocationRequest);
        return ApiResponse.ok(Map.of("message", "Workforce location updated successfully"));
    }

    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Converts the task_management payload into the legacy TaskCheckDto shape that
    // existing clients expect. Same endpoint, same response contract.
    // CLEANUP: remove after Phase E when clients are updated to the new payload format.
    private static TaskCheckDto toTaskCheckDto(TaskDefinitionPayload taskDefinition) {
        List<TaskCheckDTO> checks = taskDefinition.getChecks().stream()
            .map(WorkforceController::toTaskCheckDTO)
            .toList();
        return new TaskCheckDto(taskDefinition.getName(), checks);
    }

    private static TaskCheckDTO toTaskCheckDTO(TaskCheckDefinitionPayload checkDef) {
        return switch (checkDef.getCheckType()) {
            case "TEXT" -> {
                TaskCheckTextDTO dto = new TaskCheckTextDTO();
                dto.setId(checkDef.getId());
                dto.setName(checkDef.getName());
                dto.setEvidence(checkDef.isHasEvidence());
                dto.setCommentCheck(checkDef.isHasComment());
                if (checkDef.getCheckSettings() instanceof TextCheckValue textVal)
                    dto.setNotes(textVal.getNotes());
                yield dto;
            }
            case "NUMBER" -> {
                TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
                dto.setId(checkDef.getId());
                dto.setName(checkDef.getName());
                dto.setEvidence(checkDef.isHasEvidence());
                dto.setCommentCheck(checkDef.isHasComment());
                if (checkDef.getCheckSettings() instanceof NumberCheckValue numVal) {
                    dto.setUnit(numVal.getUnit());
                    dto.setOperator(numVal.getOperator());
                    dto.setValue(numVal.getValue());
                }
                yield dto;
            }
            case "DECIMAL" -> {
                TaskCheckDecimalDTO dto = new TaskCheckDecimalDTO();
                dto.setId(checkDef.getId());
                dto.setName(checkDef.getName());
                dto.setEvidence(checkDef.isHasEvidence());
                dto.setCommentCheck(checkDef.isHasComment());
                if (checkDef.getCheckSettings() instanceof DecimalCheckValue decVal) {
                    dto.setUnit(decVal.getUnit());
                    dto.setOperator(decVal.getOperator());
                    dto.setValue(decVal.getValue());
                }
                yield dto;
            }
            case "LIST" -> {
                TaskCheckListDTO dto = new TaskCheckListDTO();
                dto.setId(checkDef.getId());
                dto.setName(checkDef.getName());
                dto.setEvidence(checkDef.isHasEvidence());
                dto.setCommentCheck(checkDef.isHasComment());
                if (checkDef.getCheckSettings() instanceof ListCheckValue listVal)
                    dto.setListItems(listVal.getItems());
                yield dto;
            }
            default -> throw new IllegalStateException("Unknown check type: " + checkDef.getCheckType());
        };
    }
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────
}
