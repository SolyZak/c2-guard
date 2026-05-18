package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.repository.*;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskEntryRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
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

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the patrol task distribution flow in TaskDistributionServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class TaskDistributionPatrolIntegrationTest {

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
    @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Mock private LKCustomerContractOperationService serviceTime;
    @Mock private SiteDistribution siteDistribution;
    @Mock private Patrol patrol;

    private TaskDistributionServiceImpl service;

    private static final Long CUSTOMER_ID        = 1L;
    private static final Long CONTRACT_ID        = 10L;
    private static final Long SERVICE_ID         = 20L;
    private static final Long SITE_ID            = 30L;
    private static final Long SERVICE_TIME_ID    = 40L;
    private static final Long PATROL_DETAIL_ID   = 50L;
    private static final Long TASK_DEFINITION_ID = 5L;

    private Customer customer;
    private CustomerContract contract;

    @BeforeEach
    void setUp() {
        service = new TaskDistributionServiceImpl(
            customerRepository, customerContractRepository, customerContractServiceRepository,
            siteDistributionRepository, customerContractOperationServiceRepository,
            patrolDetailRepository, taskDistributionRepository,
            patrolTaskDistributionRepository, taskAssignmentRepository, locationRepository,
            attendanceClient, createScheduledTaskForDistributionService,
            taskDistributionMapper, utils, taskPresenter,
            taskExecutionSlotRepository, orgUnitClient, taskExecutionPresenter,
            eventPublisher
        );

        UserData loggedInUser = UserData.builder().id("99").customerId(CUSTOMER_ID).build();
        when(utils.getLoggedInUser()).thenReturn(loggedInUser);

        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setTimezone(CustomTimezone.SAUDI_ARABIA);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        contract = new CustomerContract();
        contract.setId(CONTRACT_ID);
        contract.setEndAgreementDate(LocalDate.now().plusDays(1));
        when(customerContractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(contract));

        when(customerContractServiceRepository.findById(SERVICE_ID))
            .thenReturn(Optional.of(new LKCustomerContractService()));

        when(siteDistributionRepository.findBySiteIdAndContractIdAndServiceId(SITE_ID, CONTRACT_ID, SERVICE_ID))
            .thenReturn(Optional.of(siteDistribution));

        when(customerContractOperationServiceRepository.findByIdAndSiteDistribution_Id(SERVICE_TIME_ID, siteDistribution.getId()))
            .thenReturn(Optional.of(serviceTime));

        when(serviceTime.getId()).thenReturn(SERVICE_TIME_ID);
        when(serviceTime.getQuantity()).thenReturn(1L);
        when(serviceTime.getDays()).thenReturn(EnumSet.allOf(WeekDaysEnum.class));
        when(serviceTime.getFromTime()).thenReturn(OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC));
        when(serviceTime.getToTime()).thenReturn(OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC));
        lenient().when(serviceTime.getSiteDistribution()).thenReturn(siteDistribution);

        when(patrolTaskDistributionRepository.existsByServiceTime_IdAndPatrolDetail_Id(any(), any()))
            .thenReturn(false);

        when(taskAssignmentRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
        when(taskDistributionRepository.saveAllAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        when(patrol.getCustomer()).thenReturn(customer);
        lenient().when(patrol.getId()).thenReturn(1L);
        when(patrol.getFrequency()).thenReturn("once");
        when(patrol.getFrequencyRate()).thenReturn("daily");
    }

    @Test
    @DisplayName("[NEW PATH] PatrolDetail.taskDefinitionId set → TaskDistribution.taskDefinitionId stored, task is null")
    void distributePatrol_withTaskDefinitionId_storesTaskDefinitionIdAndLeavesTaskNull() {
        Location location = new Location();
        location.setId(1L);

        PatrolDetail patrolDetail = PatrolDetail.builder()
            .id(PATROL_DETAIL_ID)
            .taskDefinitionId(TASK_DEFINITION_ID)
            .patrol(patrol)
            .location(location)
            .build();

        when(patrolDetailRepository.findAllById(List.of(PATROL_DETAIL_ID)))
            .thenReturn(List.of(patrolDetail));

        DistributePatrolTaskRequest request = DistributePatrolTaskRequest.builder()
            .contractId(CONTRACT_ID)
            .serviceId(SERVICE_ID)
            .distributions(List.of(
                DistributePatrolTaskEntryRequest.builder()
                    .siteId(SITE_ID)
                    .serviceTimeId(SERVICE_TIME_ID)
                    .startDate(LocalDate.now())
                    .patrolDetailIds(List.of(PATROL_DETAIL_ID))
                    .build()
            ))
            .build();

        service.distributePatrolTasks(request);

        ArgumentCaptor<List<TaskDistribution>> captor = ArgumentCaptor.forClass(List.class);
        verify(taskDistributionRepository).saveAllAndFlush(captor.capture());
        List<TaskDistribution> saved = captor.getValue();

        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getTaskDefinitionId()).isEqualTo(TASK_DEFINITION_ID);
    }
}
