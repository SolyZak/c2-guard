package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.ContractIdsRequest;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.repository.*;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.mappers.TaskDistributionMapper;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.PatrolTaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskAssignmentRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.CreateScheduledTaskForDistributionServiceImpl;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.TaskDistributionServiceImpl;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.CreateScheduledTaskForDistributionService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration test for the dual-path immediate task distribution flow.
 *
 * Covers:
 *  - [NEW PATH]         taskDefinitionId provided → TaskPresenter called, taskDefinitionId stored, task=null
 *  - [COEXISTENCE PATH] taskId provided → old path executes, TaskPresenter not called, task stored
 *  - [NEW PATH ERROR]   invalid taskDefinitionId → BusinessException propagates, nothing saved
 *  - [REQUEST LEVEL]    neither field provided → validation returns false (controller would reject)
 *
 * // [TASK-MIGRATION] NEW: this test class covers the pilot flow introduced in V56.
 * // CLEANUP: after Phase E, remove the old-path test and the taskId-related assertions.
 */
@ExtendWith(MockitoExtension.class)
// Test 4 only checks request-level validation and never calls the service, so setUp stubs are lenient
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskDistributionImmediateIntegrationTest {

    // ── Mocks ──────────────────────────────────────────────────────────────────────
    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerContractRepository customerContractRepository;
    @Mock private LKCustomerContractServiceRepository customerContractServiceRepository;
    @Mock private SiteDistributionRepository siteDistributionRepository;
    @Mock private LKCustomerContractOperationServiceRepository customerContractOperationServiceRepository;
    @Mock private PatrolDetailRepository patrolDetailRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private TaskDistributionRepository taskDistributionRepository;
    @Mock private PatrolTaskDistributionRepository patrolTaskDistributionRepository;
    @Mock private TaskAssignmentRepository taskAssignmentRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private AttendanceFeignClient attendanceClient;
    @Mock private CreateScheduledTaskForDistributionService createScheduledTaskForDistributionService;
    @Mock private TaskDistributionMapper taskDistributionMapper;
    @Mock private Utils utils;
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // Mock of the task_management ACL — the key dependency under test.
    @Mock private TaskPresenter taskPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    private TaskDistributionServiceImpl service;

    // ── Shared test fixtures ───────────────────────────────────────────────────────
    private static final Long CUSTOMER_ID        = 1L;
    private static final Long CONTRACT_ID        = 10L;
    private static final Long WORKFORCE_ID       = 100L;
    private static final Long TASK_ID            = 2L;      // legacy
    private static final Long TASK_DEFINITION_ID = 5L;      // new module

    private static final OffsetDateTime START = OffsetDateTime.now().plusHours(1);
    private static final OffsetDateTime END   = OffsetDateTime.now().plusHours(3);

    @BeforeEach
    void setUp() {
        service = new TaskDistributionServiceImpl(
            customerRepository,
            customerContractRepository,
            customerContractServiceRepository,
            siteDistributionRepository,
            customerContractOperationServiceRepository,
            patrolDetailRepository,
            taskRepository,
            taskDistributionRepository,
            patrolTaskDistributionRepository,
            taskAssignmentRepository,
            locationRepository,
            attendanceClient,
            createScheduledTaskForDistributionService,
            taskDistributionMapper,
            utils,
            taskPresenter
        );

        // Common stubs shared by all tests
        UserData loggedInUser = UserData.builder()
            .id("99")
            .customerId(CUSTOMER_ID)
            .build();
        when(utils.getLoggedInUser()).thenReturn(loggedInUser);

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        when(attendanceClient.getContractIdsForCheckedInWorkforcesToday(any()))
            .thenReturn(Set.of(CONTRACT_ID));

        CustomerContract contract = new CustomerContract();
        contract.setId(CONTRACT_ID);
        when(customerContractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(contract));

        // taskAssignmentRepository.saveAll returns its input so slots can be built
        // lenient: tests 3 & 4 throw/skip before reaching save calls
        lenient().when(taskAssignmentRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // saveAndFlush returns the distribution after setting an ID on ImmediateTaskDistribution
        // lenient: tests 3 & 4 throw/skip before reaching save calls
        lenient().when(taskDistributionRepository.saveAndFlush(any())).thenAnswer(inv -> {
            TaskDistribution dist = inv.getArgument(0);
            if (dist.getImmediateTaskDistribution() != null) {
                dist.getImmediateTaskDistribution().setId(1L);
            }
            return dist;
        });
    }

    // ── Test 1: New path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("[NEW PATH] taskDefinitionId provided: TaskPresenter called, taskDefinitionId stored, task is null")
    void distributeImmediate_withTaskDefinitionId_storesTaskDefinitionIdAndLeavesTaskNull() {
        // ─── [TASK-MIGRATION] NEW: this test validates the new integration path. ────────
        // CLEANUP: make this test the only test (rename + remove old-path guard) after Phase E.

        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
            .id(TASK_DEFINITION_ID)
            .name("Check Perimeter")
            .severity("HIGH")
            .checks(List.of())
            .build();
        when(taskPresenter.getTaskDefinition(TASK_DEFINITION_ID)).thenReturn(payload);

        DistributeImmediateTaskRequest request = DistributeImmediateTaskRequest.builder()
            .taskDefinitionId(TASK_DEFINITION_ID)
            .startDateTime(START)
            .endDateTime(END)
            .workforceIds(Set.of(WORKFORCE_ID))
            .locationName("Gate A")
            .latitude(new java.math.BigDecimal("24.7136"))
            .longitude(new java.math.BigDecimal("46.6753"))
            .build();

        service.distributeImmediateTasks(request);

        // Verify TaskPresenter was called with the correct ID
        verify(taskPresenter).getTaskDefinition(TASK_DEFINITION_ID);

        // Capture what was saved to the repository
        ArgumentCaptor<TaskDistribution> captor = ArgumentCaptor.forClass(TaskDistribution.class);
        verify(taskDistributionRepository).saveAndFlush(captor.capture());
        TaskDistribution saved = captor.getValue();

        assertThat(saved.getTaskDefinitionId()).isEqualTo(TASK_DEFINITION_ID);
        assertThat(saved.getTask()).isNull();
    }

    // ── Test 2: Old path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("[COEXISTENCE PATH] taskId provided: old path executes, TaskPresenter never called, task stored")
    void distributeImmediate_withTaskId_usesLegacyPathAndDoesNotCallPresenter() {
        // ─── [TASK-MIGRATION] COEXISTENCE: validates backward compatibility. ────────────
        // CLEANUP: remove this test after Phase E cleanup migration.

        Task task = new Task();
        task.setId(TASK_ID);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

        DistributeImmediateTaskRequest request = DistributeImmediateTaskRequest.builder()
            .taskId(TASK_ID)
            .startDateTime(START)
            .endDateTime(END)
            .workforceIds(Set.of(WORKFORCE_ID))
            .locationName("Gate B")
            .latitude(new java.math.BigDecimal("24.7136"))
            .longitude(new java.math.BigDecimal("46.6753"))
            .build();

        service.distributeImmediateTasks(request);

        // Verify TaskPresenter was NOT involved
        verifyNoInteractions(taskPresenter);

        // Capture what was saved and verify legacy task is set
        ArgumentCaptor<TaskDistribution> captor = ArgumentCaptor.forClass(TaskDistribution.class);
        verify(taskDistributionRepository).saveAndFlush(captor.capture());
        TaskDistribution saved = captor.getValue();

        assertThat(saved.getTask()).isNotNull();
        assertThat(saved.getTask().getId()).isEqualTo(TASK_ID);
        assertThat(saved.getTaskDefinitionId()).isNull();
    }

    // ── Test 3: Invalid taskDefinitionId ──────────────────────────────────────────

    @Test
    @DisplayName("[NEW PATH ERROR] invalid taskDefinitionId: BusinessException propagates, nothing saved to DB")
    void distributeImmediate_withInvalidTaskDefinitionId_throwsBusinessExceptionAndSavesNothing() {
        // ─── [TASK-MIGRATION] NEW: validates ACL error propagation. ──────────────────
        Long invalidId = 9999L;
        when(taskPresenter.getTaskDefinition(invalidId))
            .thenThrow(new BusinessException("task-not-found", HttpStatus.NOT_FOUND));

        DistributeImmediateTaskRequest request = DistributeImmediateTaskRequest.builder()
            .taskDefinitionId(invalidId)
            .startDateTime(START)
            .endDateTime(END)
            .workforceIds(Set.of(WORKFORCE_ID))
            .locationName("Gate C")
            .latitude(new java.math.BigDecimal("24.7136"))
            .longitude(new java.math.BigDecimal("46.6753"))
            .build();

        assertThatThrownBy(() -> service.distributeImmediateTasks(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("task-not-found")
            .extracting(e -> ((BusinessException) e).getHttpStatus())
            .isEqualTo(HttpStatus.NOT_FOUND);

        // Nothing should have been persisted
        verifyNoInteractions(taskDistributionRepository);
        verifyNoInteractions(taskAssignmentRepository);
    }

    // ── Test 4: Request-level validation ──────────────────────────────────────────

    @Test
    @DisplayName("[REQUEST VALIDATION] neither taskId nor taskDefinitionId provided: isTaskReferenceProvided returns false")
    void distributeImmediateRequest_withNoTaskReference_failsValidation() {
        // ─── [TASK-MIGRATION] NEW: validates cross-field @AssertTrue on the record. ────
        // CLEANUP: replace this test with a simple @NotNull check on taskDefinitionId after Phase E.

        DistributeImmediateTaskRequest request = DistributeImmediateTaskRequest.builder()
            .taskId(null)
            .taskDefinitionId(null)
            .startDateTime(START)
            .endDateTime(END)
            .workforceIds(Set.of(WORKFORCE_ID))
            .locationName("Gate D")
            .latitude(new java.math.BigDecimal("24.7136"))
            .longitude(new java.math.BigDecimal("46.6753"))
            .build();

        // The @AssertTrue method returns false — Spring's @Valid would reject this at the controller
        assertThat(request.isTaskReferenceProvided()).isFalse();
    }
}
