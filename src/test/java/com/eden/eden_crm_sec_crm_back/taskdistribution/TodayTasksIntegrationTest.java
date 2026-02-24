package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskPatrolExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.TodayTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskExecutionSlotEntryResponse;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration test for the dual-path getTodayTasks flow.
 *
 * Covers:
 *  - [NEW PATH]         taskDefinitionId set, taskName null from DB → enriched from taskPresenter
 *  - [COEXISTENCE PATH] task set, taskName from DB → returned as-is, taskPresenter not called
 *
 * // [TASK-MIGRATION] NEW: covers getTodayTasks enrichment introduced in this session.
 * // CLEANUP: after Phase E, remove COEXISTENCE test and enrichment block.
 */
@ExtendWith(MockitoExtension.class)
class TodayTasksIntegrationTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private TaskExecutionSlotRepository taskExecutionSlotRepository;
    @Mock private TaskPatrolExecutionRepository taskPatrolExecutionRepository;
    @Mock private TaskDistributionMapper taskDistributionMapper;
    @Mock private AttendanceFeignClient attendanceClient;
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    @Mock private TaskPresenter taskPresenter;
    @Mock private TaskExecutionPresenter taskExecutionPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    private DistributedTaskServiceImpl service;

    private static final Long CUSTOMER_ID        = 1L;
    private static final Long WORKFORCE_ID       = 100L;
    private static final Long TASK_DISTRIBUTION_ID = 10L;
    private static final Long TASK_DEFINITION_ID = 5L;

    @BeforeEach
    void setUp() {
        service = new DistributedTaskServiceImpl(
            customerRepository, taskExecutionSlotRepository, taskPatrolExecutionRepository,
            taskDistributionMapper, attendanceClient, taskPresenter, taskExecutionPresenter
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

    // ── Test 1: New path — task name enriched from taskPresenter ──────────────────

    @Test
    @DisplayName("[NEW PATH] taskName=null, taskDefinitionId set → task name enriched from taskPresenter")
    void getTodayTasks_withNullTaskName_enrichesNameFromTaskPresenter() {
        // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
        TodayTaskSlotProjection projection = mock(TodayTaskSlotProjection.class);
        when(projection.getTaskDistributionId()).thenReturn(TASK_DISTRIBUTION_ID);
        // getStartDateTime not stubbed: single-element list skips comparator in sorted()

        when(taskExecutionSlotRepository.findTodayTasks(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(projection));

        // Mapper returns a response with null taskName and non-null taskDefinitionId
        TodayTaskEntryResponse responseWithNullName = TodayTaskEntryResponse.builder()
            .taskId(null)
            .taskDefinitionId(TASK_DEFINITION_ID)
            .taskName(null)            // null because LEFT JOIN returned no task
            .distributionType(DistributionType.IMMEDIATE)
            .executionSlots(List.of())
            .build();

        when(taskDistributionMapper.toExecutionSlotResponseList(any())).thenReturn(List.of());
        when(taskDistributionMapper.toTodayTaskEntryResponse(any(), any())).thenReturn(responseWithNullName);

        // Presenter returns the real task name
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
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────
    }

    // ── Test 2: Coexistence path — task name from DB ──────────────────────────────

    @Test
    @DisplayName("[COEXISTENCE PATH] taskName from DB → returned as-is, taskPresenter not called")
    void getTodayTasks_withTaskNameFromDb_returnsNameDirectlyWithoutPresenter() {
        // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────
        // CLEANUP: remove this test after Phase E.
        TodayTaskSlotProjection projection = mock(TodayTaskSlotProjection.class);
        when(projection.getTaskDistributionId()).thenReturn(TASK_DISTRIBUTION_ID);
        // getStartDateTime not stubbed: single-element list skips comparator in sorted()

        when(taskExecutionSlotRepository.findTodayTasks(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(projection));

        // Mapper returns a response with a non-null taskName (old path, task joined via LEFT JOIN)
        TodayTaskEntryResponse responseWithName = TodayTaskEntryResponse.builder()
            .taskId(2L)
            .taskDefinitionId(null)
            .taskName("Guard Round")     // taskName from DB (old path)
            .distributionType(DistributionType.PATROL)
            .executionSlots(List.of())
            .build();

        when(taskDistributionMapper.toExecutionSlotResponseList(any())).thenReturn(List.of());
        when(taskDistributionMapper.toTodayTaskEntryResponse(any(), any())).thenReturn(responseWithName);

        TodayTasksRequest request = new TodayTasksRequest(1L, 2L, 3L, 1);
        TodayTasksResponse result = service.getTodayTasks(request);

        assertThat(result.tasks()).hasSize(1);
        assertThat(result.tasks().get(0).taskName()).isEqualTo("Guard Round");
        assertThat(result.tasks().get(0).taskDefinitionId()).isNull();

        // Presenter must NOT be called since task name came from DB
        verifyNoInteractions(taskPresenter);
        // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────
    }
}
