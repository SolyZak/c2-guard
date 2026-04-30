package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.Severity;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.PatrolDetail;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.ImmediateTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.tasks.DistributedTaskMissedStatusJob;
import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for DistributedTaskMissedStatusJob: status transition, description format,
 * and dual-mode (new / old path) alert dispatch.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MissedTaskAlertTest {

    @Mock TaskExecutionSlotRepository executionSlotRepository;
    @Mock CrmTriggerLogService crmTriggerLogService;
    @Mock C2AlertEventService c2AlertEventService;
    @Mock TriggerRepository triggerRepository;
    @Mock TaskPresenter taskPresenter;

    DistributedTaskMissedStatusJob job;

    Trigger trigger;
    CrmTriggerLog triggerLog;
    final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        job = new DistributedTaskMissedStatusJob(
                executionSlotRepository, crmTriggerLogService, c2AlertEventService,
                triggerRepository, taskPresenter
        );

        trigger = new Trigger();
        trigger.setId(1L);
        trigger.setCode("PATROL_TASK_MISSED");
        when(triggerRepository.findById(1L)).thenReturn(Optional.of(trigger));

        triggerLog = new CrmTriggerLog();
        triggerLog.setId(50L);
        when(crmTriggerLogService.addNewCrmTriggerLog(any())).thenReturn(triggerLog);
    }

    // ─── Status transition ────────────────────────────────────────────────────────

    @Test
    void slotStatusCurrent_changedToMissed() {
        TaskExecutionSlot slot = patrolSlot(TaskDistributionStatus.CURRENT, true);
        when(executionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(executionSlotRepository.saveAndFlush(slot)).thenReturn(slot);
        when(taskPresenter.getTaskDefinition(any()))
                .thenReturn(TaskDefinitionPayload.builder().id(42L).name("Task").severity("HIGH")
                        .checks(List.of()).build());

        job.performTask(args(1L));

        assertThat(slot.getStatus()).isEqualTo(TaskDistributionStatus.MISSED);
        verify(executionSlotRepository).saveAndFlush(slot);
    }

    @Test
    void slotStatusAlreadyMissed_noSaveAndNoAlert() {
        TaskExecutionSlot slot = patrolSlot(TaskDistributionStatus.MISSED, true);
        when(executionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));

        job.performTask(args(1L));

        verify(executionSlotRepository, never()).saveAndFlush(any());
        verify(c2AlertEventService, never()).sendNewC2AlertEventWithOverrideSeverity(any(), any(), any());
        verify(c2AlertEventService, never()).sendNewC2AlertEvent(any());
    }

    // ─── New path (taskDefinitionId != null) ─────────────────────────────────────

    @Test
    void newPath_usesOverrideSeverityFromTaskDefinition() {
        TaskExecutionSlot slot = patrolSlot(TaskDistributionStatus.CURRENT, true);
        when(executionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(executionSlotRepository.saveAndFlush(slot)).thenReturn(slot);
        when(taskPresenter.getTaskDefinition(42L))
                .thenReturn(TaskDefinitionPayload.builder().id(42L).name("Evening Rounds")
                        .severity("CRITICAL").checks(List.of()).build());

        job.performTask(args(1L));

        ArgumentCaptor<Severity> severityCaptor = ArgumentCaptor.forClass(Severity.class);
        ArgumentCaptor<Long> alertIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(c2AlertEventService).sendNewC2AlertEventWithOverrideSeverity(
                eq(triggerLog), severityCaptor.capture(), alertIdCaptor.capture());

        assertThat(severityCaptor.getValue()).isEqualTo(Severity.CRITICAL);
        assertThat(alertIdCaptor.getValue()).isEqualTo(5L);
    }

    @Test
    void newPath_oldPathSendEventNotCalled() {
        TaskExecutionSlot slot = patrolSlot(TaskDistributionStatus.CURRENT, true);
        when(executionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(executionSlotRepository.saveAndFlush(slot)).thenReturn(slot);
        when(taskPresenter.getTaskDefinition(42L))
                .thenReturn(TaskDefinitionPayload.builder().id(42L).name("Task").severity("HIGH")
                        .checks(List.of()).build());

        job.performTask(args(1L));

        verify(c2AlertEventService, never()).sendNewC2AlertEvent(any());
    }

    // ─── Description format ───────────────────────────────────────────────────────

    @Test
    void patrol_description_includesPatrolNameAndTaskName() {
        // description should be "Evening Patrol - Check Perimeter"
        TaskExecutionSlot slot = patrolSlot(TaskDistributionStatus.CURRENT, true);
        when(executionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(executionSlotRepository.saveAndFlush(slot)).thenReturn(slot);
        when(taskPresenter.getTaskDefinition(42L))
                .thenReturn(TaskDefinitionPayload.builder().id(42L).name("Check Perimeter")
                        .severity("HIGH").checks(List.of()).build());

        job.performTask(args(1L));

        ArgumentCaptor<TriggerEventDto> dtoCaptor = ArgumentCaptor.forClass(TriggerEventDto.class);
        verify(crmTriggerLogService).addNewCrmTriggerLog(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().getDescription()).isEqualTo("Evening Patrol - Check Perimeter");
    }

    @Test
    void immediate_description_isJustTaskName() {
        // Immediate distributions have no patrol name — description is only the task name
        TaskExecutionSlot slot = immediateSlot(TaskDistributionStatus.CURRENT, true);
        when(executionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(executionSlotRepository.saveAndFlush(slot)).thenReturn(slot);
        when(taskPresenter.getTaskDefinition(42L))
                .thenReturn(TaskDefinitionPayload.builder().id(42L).name("Check Perimeter")
                        .severity("HIGH").checks(List.of()).build());

        job.performTask(args(1L));

        ArgumentCaptor<TriggerEventDto> dtoCaptor = ArgumentCaptor.forClass(TriggerEventDto.class);
        verify(crmTriggerLogService).addNewCrmTriggerLog(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().getDescription()).isEqualTo("Check Perimeter");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private TaskExecutionSlot patrolSlot(TaskDistributionStatus status, boolean newPath) {
        Location location = Location.builder()
                .id(10L).longitude(new BigDecimal("55.0")).latitude(new BigDecimal("25.0")).build();

        LKCustomerContractOperationService serviceTime = mock(LKCustomerContractOperationService.class);
        SiteDistribution siteDistribution = mock(SiteDistribution.class);
        CustomerSite site = mock(CustomerSite.class);
        when(site.getId()).thenReturn(100L);
        when(siteDistribution.getSite()).thenReturn(site);
        when(serviceTime.getSiteDistribution()).thenReturn(siteDistribution);

        Patrol patrol = mock(Patrol.class);
        when(patrol.getName()).thenReturn("Evening Patrol");
        PatrolDetail patrolDetail = mock(PatrolDetail.class);
        when(patrolDetail.getPatrol()).thenReturn(patrol);

        PatrolTaskDistribution ptd = PatrolTaskDistribution.builder()
                .location(location).serviceTime(serviceTime).patrolDetail(patrolDetail).build();

        TaskDistribution taskDistribution = TaskDistribution.builder()
                .distributionType(DistributionType.PATROL)
                .taskDefinitionId(42L)
                .build();

        taskDistribution.setPatrolTaskDistribution(ptd);

        Customer customer = new Customer();
        customer.setId(1L);

        return TaskExecutionSlot.builder()
                .id(1L).status(status)
                .startDateTime(OffsetDateTime.now().minusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(1))
                .taskDistribution(taskDistribution).customer(customer)
                .build();
    }

    /** Builds an IMMEDIATE-type slot with an inline longitude/latitude (no Location entity). */
    private TaskExecutionSlot immediateSlot(TaskDistributionStatus status, boolean newPath) {
        ImmediateTaskDistribution itd = new ImmediateTaskDistribution();
        itd.setLongitude(new BigDecimal("55.0"));
        itd.setLatitude(new BigDecimal("25.0"));

        TaskDistribution taskDistribution = TaskDistribution.builder()
                .distributionType(DistributionType.IMMEDIATE)
                .taskDefinitionId(42L)
                .build();

        taskDistribution.setImmediateTaskDistribution(itd);

        Customer customer = new Customer();
        customer.setId(1L);

        return TaskExecutionSlot.builder()
                .id(1L).status(status)
                .startDateTime(OffsetDateTime.now().minusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(1))
                .taskDistribution(taskDistribution).customer(customer)
                .build();
    }

    private ObjectNode args(Long slotId) {
        ObjectNode node = mapper.createObjectNode();
        node.put("executionSlotId", slotId);
        return node;
    }
}
