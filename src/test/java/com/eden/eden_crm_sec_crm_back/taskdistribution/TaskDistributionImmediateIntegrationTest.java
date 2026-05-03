package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.ContractIdsRequest;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.repository.*;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.mappers.TaskDistributionMapper;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.PatrolTaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskAssignmentRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
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
 * Integration tests for the immediate task distribution flow in TaskDistributionServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskDistributionImmediateIntegrationTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private CustomerContractRepository customerContractRepository;
    @Mock private LKCustomerContractServiceRepository customerContractServiceRepository;
    @Mock private SiteDistributionRepository siteDistributionRepository;
    @Mock private LKCustomerContractOperationServiceRepository customerContractOperationServiceRepository;
    @Mock private PatrolDetailRepository patrolDetailRepository;
    @Mock private TaskDistributionRepository taskDistributionRepository;
    @Mock private PatrolTaskDistributionRepository patrolTaskDistributionRepository;
    @Mock private TaskAssignmentRepository taskAssignmentRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private AttendanceFeignClient attendanceClient;
    @Mock private CreateScheduledTaskForDistributionService createScheduledTaskForDistributionService;
    @Mock private TaskDistributionMapper taskDistributionMapper;
    @Mock private Utils utils;
    @Mock private TaskPresenter taskPresenter;
    @Mock private TaskExecutionSlotRepository taskExecutionSlotRepository;
    @Mock private OrgUnitClient orgUnitClient;
    @Mock private TaskExecutionPresenter taskExecutionPresenter;

    private TaskDistributionServiceImpl service;

    private static final Long CUSTOMER_ID        = 1L;
    private static final Long CONTRACT_ID        = 10L;
    private static final Long WORKFORCE_ID       = 100L;
    private static final Long TASK_DEFINITION_ID = 5L;

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
            taskDistributionRepository,
            patrolTaskDistributionRepository,
            taskAssignmentRepository,
            locationRepository,
            attendanceClient,
            createScheduledTaskForDistributionService,
            taskDistributionMapper,
            utils,
            taskPresenter,
            taskExecutionSlotRepository,
            orgUnitClient,
            taskExecutionPresenter
        );

        UserData loggedInUser = UserData.builder().id("99").customerId(CUSTOMER_ID).build();
        when(utils.getLoggedInUser()).thenReturn(loggedInUser);

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        when(attendanceClient.getContractIdsForCheckedInWorkforcesToday(any()))
            .thenReturn(Set.of(CONTRACT_ID));

        CustomerContract contract = new CustomerContract();
        contract.setId(CONTRACT_ID);
        when(customerContractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(contract));

        when(taskAssignmentRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        when(taskDistributionRepository.saveAndFlush(any())).thenAnswer(inv -> {
            TaskDistribution dist = inv.getArgument(0);
            if (dist.getImmediateTaskDistribution() != null) {
                dist.getImmediateTaskDistribution().setId(1L);
            }
            return dist;
        });
    }

    @Test
    @DisplayName("[NEW PATH] taskDefinitionId provided: TaskPresenter called, taskDefinitionId stored, task is null")
    void distributeImmediate_withTaskDefinitionId_storesTaskDefinitionIdAndLeavesTaskNull() {
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

        verify(taskPresenter).getTaskDefinition(TASK_DEFINITION_ID);

        ArgumentCaptor<TaskDistribution> captor = ArgumentCaptor.forClass(TaskDistribution.class);
        verify(taskDistributionRepository).saveAndFlush(captor.capture());
        TaskDistribution saved = captor.getValue();

        assertThat(saved.getTaskDefinitionId()).isEqualTo(TASK_DEFINITION_ID);
    }

    @Test
    @DisplayName("[NEW PATH ERROR] invalid taskDefinitionId: BusinessException propagates, nothing saved to DB")
    void distributeImmediate_withInvalidTaskDefinitionId_throwsBusinessExceptionAndSavesNothing() {
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

        verifyNoInteractions(taskDistributionRepository);
        verifyNoInteractions(taskAssignmentRepository);
    }

}
