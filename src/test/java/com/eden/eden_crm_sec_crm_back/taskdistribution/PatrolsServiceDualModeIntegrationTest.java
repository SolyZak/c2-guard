package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolRequest;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PatrolDetailRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.PatrolsServiceImpl;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the addPatrol flow in PatrolsServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class PatrolsServiceDualModeIntegrationTest {

    @Mock private PatrolRepository patrolRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private PatrolDetailRepository patrolDetailRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private Utils utils;
    @Mock private TaskPresenter taskPresenter;

    private PatrolsServiceImpl service;

    private static final Long CUSTOMER_ID        = 1L;
    private static final Long LOCATION_ID        = 10L;
    private static final Long TASK_DEFINITION_ID = 5L;

    @BeforeEach
    void setUp() {
        service = new PatrolsServiceImpl(
            patrolRepository, locationRepository,
            patrolDetailRepository, customerRepository, utils, taskPresenter
        );

        UserData loggedInUser = UserData.builder().id("99").customerId(CUSTOMER_ID).build();
        when(utils.getLoggedInUser()).thenReturn(loggedInUser);

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        Location location = new Location();
        location.setId(LOCATION_ID);
        when(locationRepository.findAllById(List.of(LOCATION_ID))).thenReturn(List.of(location));
    }

    @Test
    @DisplayName("[NEW PATH] taskDefinitionIds provided → PatrolDetail.taskDefinitionId stored, task is null")
    void addPatrol_withTaskDefinitionIds_storesTaskDefinitionIdAndLeavesTaskNull() {
        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
            .id(TASK_DEFINITION_ID)
            .name("Check Perimeter")
            .build();
        when(taskPresenter.getTaskDefinition(TASK_DEFINITION_ID)).thenReturn(payload);

        AddPatrolDetailRequest detailRequest = new AddPatrolDetailRequest();
        detailRequest.setLocations(List.of(LOCATION_ID));
        detailRequest.setTaskDefinitionIds(List.of(TASK_DEFINITION_ID));

        AddPatrolRequest request = buildPatrolRequest(List.of(detailRequest));

        ArgumentCaptor<Patrol> patrolCaptor = ArgumentCaptor.forClass(Patrol.class);

        service.addPatrol(request);

        verify(patrolRepository).save(patrolCaptor.capture());
        Patrol savedPatrol = patrolCaptor.getValue();

        assertThat(savedPatrol.getPatrolDetails()).hasSize(1);
        PatrolDetail detail = savedPatrol.getPatrolDetails().get(0);
        assertThat(detail.getTaskDefinitionId()).isEqualTo(TASK_DEFINITION_ID);

        verify(taskPresenter).getTaskDefinition(TASK_DEFINITION_ID);
    }

    private static AddPatrolRequest buildPatrolRequest(List<AddPatrolDetailRequest> details) {
        AddPatrolRequest request = new AddPatrolRequest();
        request.setPatrolName("Evening Round");
        request.setFrequency("every-period");
        request.setFrequencyRate("30");
        request.setDetails(details);
        return request;
    }
}
