package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.ImmediateTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.tasks.DistributedTaskMissedStatusJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration test for the dual-path missed-task alert flow.
 *
 * Covers:
 *  - [NEW PATH]         taskDefinitionId set → task name fetched from taskPresenter for alert description
 *  - [COEXISTENCE PATH] taskDefinitionId null → task name read from legacy Task entity
 *
 * // [TASK-MIGRATION] NEW: covers missed-job dual-mode introduced in this session.
 * // CLEANUP: after Phase E, remove COEXISTENCE test and old-path branch.
 */
@ExtendWith(MockitoExtension.class)
class DistributedTaskMissedStatusJobIntegrationTest {

    @Mock private TaskExecutionSlotRepository repository;
    @Mock private CrmTriggerLogService crmTriggerLogService;
    @Mock private C2AlertEventService c2AlertEventService;
    @Mock private TriggerRepository triggerRepository;
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    @Mock private TaskPresenter taskPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    @Mock private ImmediateTaskDistribution immediateTaskDistribution;
    @Mock private TaskDistribution taskDistribution;
    @Mock private Trigger trigger;
    @Mock private CrmTriggerLog crmTriggerLog;

    private DistributedTaskMissedStatusJob job;

    private static final Long SLOT_ID            = 1L;
    private static final Long TASK_DEFINITION_ID = 5L;

    @BeforeEach
    void setUp() {
        job = new DistributedTaskMissedStatusJob(
            repository, crmTriggerLogService, c2AlertEventService,
            triggerRepository, taskPresenter
        );

        // Common slot setup
        Customer customer = new Customer();
        customer.setId(10L);
        customer.setTimezone(CustomTimezone.SAUDI_ARABIA);

        TaskExecutionSlot slot = TaskExecutionSlot.builder()
            .id(SLOT_ID)
            .status(TaskDistributionStatus.CURRENT)
            .startDateTime(OffsetDateTime.now().minusHours(1))
            .endDateTime(OffsetDateTime.now().plusHours(1))
            .taskDistribution(taskDistribution)
            .customer(customer)
            .build();

        when(repository.findById(SLOT_ID)).thenReturn(Optional.of(slot));
        when(repository.saveAndFlush(any())).thenReturn(slot);

        // Use the @Mock trigger field — stub getId() and getCode() needed by TriggerEventDto builder
        when(trigger.getId()).thenReturn(1L);
        when(trigger.getCode()).thenReturn("PATROL_TASK_MISSED");
        when(triggerRepository.findById(1L)).thenReturn(Optional.of(trigger));
        when(crmTriggerLogService.addNewCrmTriggerLog(any())).thenReturn(crmTriggerLog);
        doNothing().when(c2AlertEventService).sendNewC2AlertEvent(any());

        // Immediate distribution with explicit coordinates (no location object)
        when(immediateTaskDistribution.getLocation()).thenReturn(null);
        when(immediateTaskDistribution.getLatitude()).thenReturn(new BigDecimal("24.7136"));
        when(immediateTaskDistribution.getLongitude()).thenReturn(new BigDecimal("46.6753"));

        when(taskDistribution.getDistributionType()).thenReturn(DistributionType.IMMEDIATE);
        when(taskDistribution.getImmediateTaskDistribution()).thenReturn(immediateTaskDistribution);
        // patrolTaskDistribution not stubbed — mock default (null) is already correct for IMMEDIATE type
    }

    // ── Test 1: New path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("[NEW PATH] taskDefinitionId set → task name fetched from taskPresenter in alert description")
    void performTask_withTaskDefinitionId_usesPresenterForTaskName() {
        // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
        when(taskDistribution.getTaskDefinitionId()).thenReturn(TASK_DEFINITION_ID);

        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
            .id(TASK_DEFINITION_ID)
            .name("Check Perimeter")
            .build();
        when(taskPresenter.getTaskDefinition(TASK_DEFINITION_ID)).thenReturn(payload);

        ObjectNode args = new ObjectMapper().createObjectNode();
        args.put("executionSlotId", SLOT_ID);

        job.performTask(args);

        verify(taskPresenter).getTaskDefinition(TASK_DEFINITION_ID);

        ArgumentCaptor<TriggerEventDto> captor = ArgumentCaptor.forClass(TriggerEventDto.class);
        verify(crmTriggerLogService).addNewCrmTriggerLog(captor.capture());
        assertThat(captor.getValue().getDescription()).startsWith("Check Perimeter | ");
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────
    }

    // ── Test 2: Coexistence path ───────────────────────────────────────────────────

    @Test
    @DisplayName("[COEXISTENCE PATH] taskDefinitionId null → task name read from legacy Task entity")
    void performTask_withLegacyTask_usesTaskEntityNameAndDoesNotCallPresenter() {
        // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────
        // CLEANUP: remove this test after Phase E.
        when(taskDistribution.getTaskDefinitionId()).thenReturn(null);

        com.eden.eden_crm_sec_crm_back.models.Task legacyTask =
            new com.eden.eden_crm_sec_crm_back.models.Task();
        legacyTask.setId(2L);
        legacyTask.setName("Guard Round");
        when(taskDistribution.getTask()).thenReturn(legacyTask);

        ObjectNode args = new ObjectMapper().createObjectNode();
        args.put("executionSlotId", SLOT_ID);

        job.performTask(args);

        // Presenter must NOT be called
        verifyNoInteractions(taskPresenter);

        ArgumentCaptor<TriggerEventDto> captor = ArgumentCaptor.forClass(TriggerEventDto.class);
        verify(crmTriggerLogService).addNewCrmTriggerLog(captor.capture());
        assertThat(captor.getValue().getDescription()).startsWith("Guard Round | ");
        // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────
    }
}
