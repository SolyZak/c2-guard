package com.eden.eden_crm_sec_crm_back.taskdistribution.services.base;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.AvailableServiceTimesRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributableTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ImmediateTasksReportRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.AvailableServiceTimeResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.DistributableTaskResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.ImmediateTaskReportEntryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskDistributionService {
    void distributePatrolTasks(DistributePatrolTaskRequest distributePatrolTaskRequest);
    void distributeImmediateTasks(DistributeImmediateTaskRequest distributeImmediateTaskRequest);
    List<AvailableServiceTimeResponse> getAllAvailableServiceTimes(AvailableServiceTimesRequest availableServiceTimesRequest);
    List<DistributableTaskResponse> getDistributableTasks(DistributableTasksRequest distributableTasksRequest);
    Page<ImmediateTaskReportEntryDto> getImmediateTasksReport(ImmediateTasksReportRequest request, Pageable pageable);
}
