package com.eden.eden_crm_sec_crm_back.patrols;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceGateway;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressResponse;
import com.eden.eden_crm_sec_crm_back.locks.services.CustomerEditLockService;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditSaveResponse;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.patrols.services.PatrolVersionAuditService;
import com.eden.eden_crm_sec_crm_back.patrols.services.impl.PatrolEditServiceImpl;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PatrolDetailRepository;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.FlexSchedulerCleanupRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.PatrolTaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.TaskDistributionService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatrolEditServiceTest {

    @Mock private PatrolRepository patrolRepository;
    @Mock private PatrolDetailRepository patrolDetailRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private ContractOperationSiteDistributionPatrolRepository bindingRepository;
    @Mock private PatrolTaskDistributionRepository patrolTaskDistributionRepository;
    @Mock private TaskDistributionRepository taskDistributionRepository;
    @Mock private TaskExecutionSlotRepository taskExecutionSlotRepository;
    @Mock private FlexSchedulerCleanupRepository flexSchedulerCleanupRepository;
    @Mock private TaskDistributionService taskDistributionService;
    @Mock private PatrolVersionAuditService auditService;
    @Mock private CustomerEditLockService lockService;
    @Mock private AttendanceGateway attendanceGateway;
    @Mock private TaskPresenter taskPresenter;
    @Mock private Utils utils;

    private PatrolEditServiceImpl service;

    private Patrol patrol;
    private Customer customer;

    @BeforeEach
    void setUp() {
        service = new PatrolEditServiceImpl(
                patrolRepository, patrolDetailRepository, locationRepository,
                bindingRepository, patrolTaskDistributionRepository, taskDistributionRepository,
                taskExecutionSlotRepository, flexSchedulerCleanupRepository, taskDistributionService,
                auditService, lockService, attendanceGateway, taskPresenter, utils);

        customer = new Customer();
        customer.setId(1L);

        Premise premise = new Premise();
        premise.setName("HQ");

        Location loc1 = new Location();
        loc1.setId(17L);
        loc1.setName("Main Gate");
        loc1.setPremise(premise);

        PatrolDetail pd1 = new PatrolDetail();
        pd1.setId(100L);
        pd1.setLocation(loc1);
        pd1.setTaskDefinitionId(11L);
        pd1.setDisplayOrder(1);

        patrol = new Patrol();
        patrol.setId(75L);
        patrol.setName("Evening Sweep");
        patrol.setFrequency("once");
        patrol.setFrequencyRate("daily");
        patrol.setValidFrom(LocalDate.of(2026, 1, 1));
        patrol.setValidTo(null);
        patrol.setCustomer(customer);
        patrol.setPatrolDetails(new ArrayList<>(List.of(pd1)));
        pd1.setPatrol(patrol);
    }

    @Test
    void getForEdit_returnsPatrolWithLocationsAndTasks() {
        when(patrolRepository.findById(75L)).thenReturn(Optional.of(patrol));

        PatrolEditResponse resp = service.getForEdit(75L);

        assertThat(resp.getPatrolId()).isEqualTo(75L);
        assertThat(resp.getName()).isEqualTo("Evening Sweep");
        assertThat(resp.getFrequency()).isEqualTo("once");
        assertThat(resp.getDetails()).hasSize(1);
        assertThat(resp.getDetails().get(0).getLocationId()).isEqualTo(17L);
        assertThat(resp.getDetails().get(0).getLocationName()).isEqualTo("Main Gate");
        assertThat(resp.getDetails().get(0).getTasks()).hasSize(1);
        assertThat(resp.getDetails().get(0).getTasks().get(0).getTaskDefinitionId()).isEqualTo(11L);
    }

    @Test
    void save_nameOnlyChange_doesNotCreateNewVersion() {
        when(patrolRepository.findById(75L)).thenReturn(Optional.of(patrol));
        when(utils.getLoggedInUser()).thenReturn(
                UserData.builder().id("100").name("Ahmed").customerId(1L).type(UserType.CUSTOMER).build());
        when(bindingRepository.findActiveBindingsByPatrolId(eq(75L), any())).thenReturn(List.of());
        lenient().when(auditService.record(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        PatrolEditRequest req = PatrolEditRequest.builder()
                .name("Evening Sweep RENAMED")
                .frequency("once")
                .frequencyRate("daily")
                .build();

        PatrolEditSaveResponse resp = service.save(75L, req);

        assertThat(resp.getNewPatrolId()).isEqualTo(75L);
        assertThat(resp.getPreviousPatrolId()).isEqualTo(75L);
        verify(patrolRepository, times(1)).save(patrol);
        assertThat(patrol.getName()).isEqualTo("Evening Sweep RENAMED");
        // validTo should NOT be set (no versioning).
        assertThat(patrol.getValidTo()).isNull();
    }

    @Test
    void save_frequencyChange_createsNewVersion() {
        when(patrolRepository.findById(75L)).thenReturn(Optional.of(patrol));
        when(utils.getLoggedInUser()).thenReturn(
                UserData.builder().id("100").name("Ahmed").customerId(1L).type(UserType.CUSTOMER).build());
        when(bindingRepository.findActiveBindingsByPatrolId(eq(75L), any())).thenReturn(List.of());
        when(patrolRepository.saveAndFlush(any(Patrol.class))).thenAnswer(inv -> {
            Patrol p = inv.getArgument(0);
            p.setId(76L); // simulate ID generation
            // Stub findById for the new patrol so getForEdit(76) works in the audit step.
            lenient().when(patrolRepository.findById(76L)).thenReturn(Optional.of(p));
            return p;
        });
        lenient().when(auditService.record(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        PatrolEditRequest req = PatrolEditRequest.builder()
                .name("Evening Sweep")
                .frequency("every-period")
                .frequencyRate("120")
                .build();

        PatrolEditSaveResponse resp = service.save(75L, req);

        assertThat(resp.getNewPatrolId()).isEqualTo(76L);
        assertThat(resp.getPreviousPatrolId()).isEqualTo(75L);
        // Old patrol's validTo should be set.
        assertThat(patrol.getValidTo()).isNotNull();
        verify(lockService).release(75L);
    }
}
