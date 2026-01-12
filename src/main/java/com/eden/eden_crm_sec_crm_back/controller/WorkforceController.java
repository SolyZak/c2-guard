package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteJobDescResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.dto.request.task.AddTaskDistributionRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.DistributionTimesWithQuantity;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.dto.response.TodayTasksResponseDto;
import com.eden.eden_crm_sec_crm_back.service.ContractDistributeService;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final TaskService taskService;
    private final ContractDistributeService contractDistributeService;

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
    @GetMapping("/today/{contractId}/{serviceId}/{siteId}/{periodId}")
    public ApiResponse<TodayTasksResponseDto> getTodayTasks(@PathVariable("contractId") Long contractId, @PathVariable("serviceId") Long serviceId, @PathVariable("siteId") Long siteId, @PathVariable("periodId") String periodId) {
        return ApiResponse.ok(taskService.getTodayTasks(contractId, serviceId, siteId, periodId));
    }

    @Operation(summary = "Get times for a distribution (repeat with quantity more than one)")
    @GetMapping("/distribute/quantity/{contractId}/{serviceId}/{siteId}")
    public ApiResponse<List<DistributionTimesWithQuantity>> getTimesForDistributionWithQuantity(
            @PathVariable("contractId") Long contractId,
            @PathVariable("serviceId") Long serviceId,
            @PathVariable("siteId") Long siteId
    ) {
        return ApiResponse.ok(contractDistributeService.getAllStartEndTimesForDistributionWithQuantity(contractId, serviceId, siteId));
    }

    @GetMapping("/{taskId}")
    public ApiResponse<TaskCheckDto> getTaskById(@PathVariable("taskId") Long taskId) {
        return ApiResponse.ok(taskService.getTaskById(taskId));
    }

    @PostMapping("/execute")
    public ApiResponse<TaskCheckDto> executeTask(@Valid @RequestBody AddTaskDistributionRequest request) {
        taskService.executeTask(request);
        return ApiResponse.created();
    }
}
