package com.eden.eden_crm_sec_crm_back.taskdistribution.services.base;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.AvailableServiceTimesRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributableTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ImmediateTasksReportRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.AvailableServiceTimeResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.DistributableTaskResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.ImmediateTaskCheckDetailDto;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.ImmediateTaskReportDetailResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.ImmediateTaskReportEntryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;

import java.time.LocalDate;
import java.util.List;

public interface TaskDistributionService {
    void distributePatrolTasks(DistributePatrolTaskRequest distributePatrolTaskRequest);

    /**
     * Creates a single PatrolTaskDistribution for one (patrolDetail, taskDefinitionId)
     * pair, including all execution slots from {@code startDate} to the contract
     * end. Used by the US2 assignment-edit ADD path to avoid duplicating the
     * distribution creation logic.
     */
    TaskDistribution createSinglePatrolDistribution(
            Customer customer,
            CustomerContract contract,
            LKCustomerContractService service,
            LKCustomerContractOperationService serviceTime,
            PatrolDetail patrolDetail,
            Patrol patrol,
            Long taskDefinitionId,
            LocalDate startDate
    );

    /**
     * Replaces the future ({@code >= fromDate}) execution slots of an existing
     * PatrolTaskDistribution with freshly generated ones using the patrol's
     * current frequency/rate, preserving past/in-progress slots. Used by the
     * US1 in-place patrol edit when the frequency changes.
     */
    void regenerateFutureSlots(
            com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution patrolTaskDistribution,
            LocalDate fromDate
    );
    void distributeImmediateTasks(DistributeImmediateTaskRequest distributeImmediateTaskRequest);
    List<AvailableServiceTimeResponse> getAllAvailableServiceTimes(AvailableServiceTimesRequest availableServiceTimesRequest);
    List<DistributableTaskResponse> getDistributableTasks(DistributableTasksRequest distributableTasksRequest);
    Page<ImmediateTaskReportEntryDto> getImmediateTasksReport(ImmediateTasksReportRequest request, Pageable pageable);
    ImmediateTaskReportDetailResponse getImmediateTaskDetails(Long executionSlotId);
}
