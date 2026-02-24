package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckTextDTO;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckText;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskPatrolExecutionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.CreateTaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TextCheckValue;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ExecuteDistributedTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.mappers.TaskDistributionMapper;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.DistributedTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration test for the dual-path task execution flow.
 *
 * Covers:
 *  - [NEW PATH]         taskDefinitionId set → TaskExecution created via presenter, slot.newTaskExecutionId stored
 *  - [COEXISTENCE PATH] taskDefinitionId null → TaskPatrolExecution created via old model
 *  - [ERROR]            check count mismatch → BusinessException
 *
 * // [TASK-MIGRATION] NEW: covers executeTask dual-mode introduced in this session.
 * // CLEANUP: after Phase E, remove COEXISTENCE test and old-path code.
 */
@ExtendWith(MockitoExtension.class)
class ExecuteDistributedTaskIntegrationTest {

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
    private static final Long SLOT_ID            = 200L;
    private static final Long TASK_DEFINITION_ID = 5L;
    private static final Long TASK_ID            = 2L;
    private static final Long TASK_EXECUTION_ID  = 300L;
    private static final Long CHECK_DEF_ID       = 10L;

    private static final OffsetDateTime START = OffsetDateTime.now().minusHours(1);
    private static final OffsetDateTime END   = OffsetDateTime.now().plusHours(1);

    @BeforeEach
    void setUp() {
        service = new DistributedTaskServiceImpl(
            customerRepository, taskExecutionSlotRepository, taskPatrolExecutionRepository,
            taskDistributionMapper, attendanceClient, taskPresenter, taskExecutionPresenter
        );

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);

        CheckInData checkInData = CheckInData.builder()
            .customerId(CUSTOMER_ID)
            .workforceId(WORKFORCE_ID)
            .build();

        when(attendanceClient.checkInData()).thenReturn(checkInData);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    }

    // ── Test 1: New path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("[NEW PATH] taskDefinitionId set → TaskExecution created via presenter, slot.newTaskExecutionId stored")
    void executeTask_withTaskDefinitionId_usesPresenterAndStoresNewTaskExecutionId() {
        // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
        TaskDistribution distribution = TaskDistribution.builder()
            .taskDefinitionId(TASK_DEFINITION_ID)
            .build();

        TaskExecutionSlot slot = TaskExecutionSlot.builder()
            .id(SLOT_ID)
            .status(TaskDistributionStatus.CURRENT)
            .startDateTime(START)
            .endDateTime(END)
            .taskDistribution(distribution)
            .build();

        when(taskExecutionSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(slot));

        TaskCheckDefinitionPayload checkDef = TaskCheckDefinitionPayload.builder()
            .id(CHECK_DEF_ID)
            .checkType("TEXT")
            .checkSettings(new TextCheckValue("template notes"))
            .build();

        TaskDefinitionPayload taskDefinition = TaskDefinitionPayload.builder()
            .id(TASK_DEFINITION_ID)
            .name("Check Perimeter")
            .checks(List.of(checkDef))
            .build();

        when(taskPresenter.getTaskDefinition(TASK_DEFINITION_ID)).thenReturn(taskDefinition);

        TaskExecutionPayload taskExecution = TaskExecutionPayload.builder()
            .id(TASK_EXECUTION_ID)
            .build();
        when(taskExecutionPresenter.createTaskExecution(any())).thenReturn(taskExecution);
        when(taskExecutionPresenter.submitTaskCheckExecution(any())).thenReturn(null);

        TaskCheckTextDTO check = new TaskCheckTextDTO();
        check.setName("Check item");
        check.setEvidence(false);
        check.setCommentCheck(false);
        check.setNotes("All clear");

        ExecuteDistributedTaskRequest request = ExecuteDistributedTaskRequest.builder()
            .executionSlotId(SLOT_ID)
            .checks(List.of(check))
            .build();

        service.executeTask(request);

        // Verify presenter was called to create the task execution
        ArgumentCaptor<CreateTaskExecutionPayload> createCaptor =
            ArgumentCaptor.forClass(CreateTaskExecutionPayload.class);
        verify(taskExecutionPresenter).createTaskExecution(createCaptor.capture());
        assertThat(createCaptor.getValue().getWorkforceId()).isEqualTo(WORKFORCE_ID);
        assertThat(createCaptor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID);

        // Verify check submission included the right values
        ArgumentCaptor<SubmitTaskCheckExecutionPayload> submitCaptor =
            ArgumentCaptor.forClass(SubmitTaskCheckExecutionPayload.class);
        verify(taskExecutionPresenter).submitTaskCheckExecution(submitCaptor.capture());
        SubmitTaskCheckExecutionPayload submitted = submitCaptor.getValue();
        assertThat(submitted.getTaskCheckDefinitionId()).isEqualTo(CHECK_DEF_ID);
        assertThat(submitted.getTaskExecutionId()).isEqualTo(TASK_EXECUTION_ID);
        assertThat(submitted.getCheckType()).isEqualTo("TEXT");
        assertThat(submitted.getCheckValues()).isInstanceOf(TextCheckValue.class);

        // Slot state updated correctly
        assertThat(slot.getNewTaskExecutionId()).isEqualTo(TASK_EXECUTION_ID);
        assertThat(slot.getStatus()).isEqualTo(TaskDistributionStatus.FINISHED);
        assertThat(slot.getExecutedByWorkforceId()).isEqualTo(WORKFORCE_ID);

        // Old model must NOT have been touched
        verifyNoInteractions(taskPatrolExecutionRepository);
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────
    }

    // ── Test 2: Coexistence path ───────────────────────────────────────────────────

    @Test
    @DisplayName("[COEXISTENCE PATH] taskDefinitionId null → TaskPatrolExecution created via old model")
    void executeTask_withLegacyTask_usesOldModelAndDoesNotCallPresenter() {
        // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────
        // CLEANUP: remove this test after Phase E.
        TaskCheckText checkTemplate = new TaskCheckText();
        checkTemplate.setName("Check item");
        checkTemplate.setEvidence(false);
        checkTemplate.setCommentCheck(false);

        Task task = new Task();
        task.setId(TASK_ID);
        task.setName("Guard Round");
        task.setTaskChecks(List.of(checkTemplate));

        TaskDistribution distribution = TaskDistribution.builder()
            .task(task)
            .build();

        TaskExecutionSlot slot = TaskExecutionSlot.builder()
            .id(SLOT_ID)
            .status(TaskDistributionStatus.CURRENT)
            .startDateTime(START)
            .endDateTime(END)
            .taskDistribution(distribution)
            .build();

        when(taskExecutionSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(slot));

        TaskPatrolExecution savedExecution = new TaskPatrolExecution();
        when(taskPatrolExecutionRepository.save(any())).thenReturn(savedExecution);

        TaskCheckTextDTO check = new TaskCheckTextDTO();
        check.setName("Check item");
        check.setEvidence(false);
        check.setCommentCheck(false);
        check.setNotes("All clear");

        ExecuteDistributedTaskRequest request = ExecuteDistributedTaskRequest.builder()
            .executionSlotId(SLOT_ID)
            .checks(List.of(check))
            .build();

        service.executeTask(request);

        // Old model was used
        verify(taskPatrolExecutionRepository).save(any());
        assertThat(slot.getTaskExecution()).isEqualTo(savedExecution);
        assertThat(slot.getStatus()).isEqualTo(TaskDistributionStatus.FINISHED);

        // New presenters must NOT have been called
        verifyNoInteractions(taskPresenter);
        verifyNoInteractions(taskExecutionPresenter);
        // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────
    }

    // ── Test 3: Check count mismatch ──────────────────────────────────────────────

    @Test
    @DisplayName("[NEW PATH ERROR] Check count mismatch → BusinessException, nothing saved")
    void executeTask_withCheckCountMismatch_throwsBusinessException() {
        // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
        TaskDistribution distribution = TaskDistribution.builder()
            .taskDefinitionId(TASK_DEFINITION_ID)
            .build();

        TaskExecutionSlot slot = TaskExecutionSlot.builder()
            .id(SLOT_ID)
            .status(TaskDistributionStatus.CURRENT)
            .startDateTime(START)
            .endDateTime(END)
            .taskDistribution(distribution)
            .build();

        when(taskExecutionSlotRepository.findById(SLOT_ID)).thenReturn(Optional.of(slot));

        // Task definition has 2 checks but request only sends 1
        TaskDefinitionPayload taskDefinition = TaskDefinitionPayload.builder()
            .id(TASK_DEFINITION_ID)
            .checks(List.of(
                TaskCheckDefinitionPayload.builder().id(1L).checkType("TEXT").build(),
                TaskCheckDefinitionPayload.builder().id(2L).checkType("NUMBER").build()
            ))
            .build();
        when(taskPresenter.getTaskDefinition(TASK_DEFINITION_ID)).thenReturn(taskDefinition);

        TaskCheckTextDTO singleCheck = new TaskCheckTextDTO();
        singleCheck.setNotes("note");

        ExecuteDistributedTaskRequest request = ExecuteDistributedTaskRequest.builder()
            .executionSlotId(SLOT_ID)
            .checks(List.of(singleCheck))
            .build();

        assertThatThrownBy(() -> service.executeTask(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Task check size not matched");

        // Execution presenter must NOT have been called
        verifyNoInteractions(taskExecutionPresenter);
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────
    }
}
