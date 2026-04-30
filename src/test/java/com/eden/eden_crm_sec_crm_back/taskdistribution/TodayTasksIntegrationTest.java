package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.TodayTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTasksResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.mappers.TaskDistributionMapper;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.TodayTaskSlotProjection;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.DistributedTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the getTodayTasks task name enrichment in DistributedTaskServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class TodayTasksIntegrationTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private TaskExecutionSlotRepository taskExecutionSlotRepository;
    @Mock private TaskDistributionMapper taskDistributionMapper;
    @Mock private AttendanceFeignClient attendanceClient;
    @Mock private TaskPresenter taskPresenter;
    @Mock private TaskExecutionPresenter taskExecutionPresenter;
    @Mock private TriggerRepository triggerRepository;
    @Mock private CrmTriggerLogService crmTriggerLogService;
    @Mock private C2AlertEventService c2AlertEventService;

    private DistributedTaskServiceImpl service;

    private static final Long CUSTOMER_ID          = 1L;
    private static final Long WORKFORCE_ID         = 100L;
    private static final Long TASK_DISTRIBUTION_ID = 10L;
    private static final Long TASK_DEFINITION_ID   = 5L;

    @BeforeEach
    void setUp() {
        service = new DistributedTaskServiceImpl(
            customerRepository, taskExecutionSlotRepository,
            taskDistributionMapper, attendanceClient, taskPresenter, taskExecutionPresenter,
            triggerRepository, crmTriggerLogService, c2AlertEventService
        );

        CheckInData checkInData = CheckInData.builder()
            .customerId(CUSTOMER_ID)
            .workforceId(WORKFORCE_ID)
            .build();

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);

        when(attendanceClient.checkInData()).thenReturn(checkInData);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    }

    @Test
    @DisplayName("[NEW PATH] taskName=null, taskDefinitionId set → task name enriched from taskPresenter")
    void getTodayTasks_withNullTaskName_enrichesNameFromTaskPresenter() {
        TodayTaskSlotProjection projection = mock(TodayTaskSlotProjection.class);
        when(projection.getTaskDistributionId()).thenReturn(TASK_DISTRIBUTION_ID);

        when(taskExecutionSlotRepository.findTodayTasks(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(projection));

        TodayTaskEntryResponse responseWithNullName = TodayTaskEntryResponse.builder()
            .taskId(null)
            .taskDefinitionId(TASK_DEFINITION_ID)
            .taskName(null)
            .distributionType(DistributionType.IMMEDIATE)
            .executionSlots(List.of())
            .build();

        when(taskDistributionMapper.toExecutionSlotResponseList(any())).thenReturn(List.of());
        when(taskDistributionMapper.toTodayTaskEntryResponse(any(), any())).thenReturn(responseWithNullName);

        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
            .id(TASK_DEFINITION_ID)
            .name("Check Perimeter")
            .checks(List.of())
            .build();
        when(taskPresenter.getTaskDefinition(TASK_DEFINITION_ID)).thenReturn(payload);

        TodayTasksRequest request = new TodayTasksRequest(1L, 2L, 3L, 1);
        TodayTasksResponse result = service.getTodayTasks(request);

        assertThat(result.tasks()).hasSize(1);
        assertThat(result.tasks().get(0).taskName()).isEqualTo("Check Perimeter");
        assertThat(result.tasks().get(0).taskDefinitionId()).isEqualTo(TASK_DEFINITION_ID);

        verify(taskPresenter).getTaskDefinition(TASK_DEFINITION_ID);
    }
}
