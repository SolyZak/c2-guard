package com.eden.eden_crm_sec_crm_back.taskdistribution.controllers;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
// Lock is per-patrol; enforced in the service layer via lockService.requireHeldOrAbsent(patrolId).
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentDeltaRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentLookupResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.assignment.AssignmentPatchResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.AvailableServiceTimesRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributableTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ImmediateTasksReportRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.AvailableServiceTimeResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.DistributableTaskResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.ImmediateTaskReportDetailResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.ImmediateTaskReportEntryDto;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.AssignmentEditService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.TaskDistributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/task-distribution")
public class TaskDistributionCustomerController {

    private final TaskDistributionService taskDistributionService;
    private final AssignmentEditService assignmentEditService;

    /** US2: read the per-(service,patrol,period,site) assignment grouped by Location. */
    @GetMapping("/patrol/assignment")
    public ApiResponse<AssignmentLookupResponse> getAssignment(
            @RequestParam Long serviceId,
            @RequestParam Long patrolId,
            @RequestParam Long serviceTimeId,
            @RequestParam Long siteId
    ) {
        return ApiResponse.ok(assignmentEditService.get(serviceId, patrolId, serviceTimeId, siteId));
    }

    /** US2: apply add/remove delta to the assignment. Lock enforced in service layer. */
    @PatchMapping("/patrol/assignment")
    public ApiResponse<AssignmentPatchResponse> patchAssignment(
            @Valid @RequestBody AssignmentDeltaRequest request
    ) {
        return ApiResponse.ok(assignmentEditService.applyDelta(request));
    }

    @PostMapping("/patrol")
    public ApiResponse<String> distributePatrolTasks(@Valid @RequestBody DistributePatrolTaskRequest distributePatrolTaskRequest) {
        taskDistributionService.distributePatrolTasks(distributePatrolTaskRequest);
        return ApiResponse.ok("Patrol tasks distributed successfully");
    }

    @PostMapping("/immediate")
    public ApiResponse<String> distributeImmediateTasks(@Valid @RequestBody DistributeImmediateTaskRequest distributeImmediateTaskRequest) {
        taskDistributionService.distributeImmediateTasks(distributeImmediateTaskRequest);
        return ApiResponse.ok("Immediate tasks distributed successfully");
    }

    @PostMapping("/available-service-times")
    public ApiResponse<List<AvailableServiceTimeResponse>> getAllAvailableServiceTimes(@Valid @RequestBody AvailableServiceTimesRequest availableServiceTimesRequest) {
        return ApiResponse.ok(taskDistributionService.getAllAvailableServiceTimes(availableServiceTimesRequest));
    }

    @PostMapping("/distributable-tasks")
    public ApiResponse<List<DistributableTaskResponse>> getDistributableTasks(@Valid @RequestBody DistributableTasksRequest distributableTasksRequest) {
        return ApiResponse.ok(taskDistributionService.getDistributableTasks(distributableTasksRequest));
    }

    @PostMapping("/immediate-tasks-report")
    public ApiResponse<Page<ImmediateTaskReportEntryDto>> getImmediateTasksReport(
            @Valid @RequestBody ImmediateTasksReportRequest request,
            Pageable pageable) {
        return ApiResponse.ok(taskDistributionService.getImmediateTasksReport(request, pageable));
    }

    @GetMapping("/immediate-tasks-report/{executionSlotId}/details")
    public ApiResponse<ImmediateTaskReportDetailResponse> getImmediateTaskDetails(
            @PathVariable Long executionSlotId) {
        return ApiResponse.ok(taskDistributionService.getImmediateTaskDetails(executionSlotId));
    }
}
