package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.Severity;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDecimalDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckListDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckNumberDTO;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.DecimalCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.ListCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.NumberCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskExecutionPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckComparisonPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckExecutionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskExecutionPayload;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ExecuteDistributedTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.mappers.TaskDistributionMapper;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.DistributedTaskServiceImpl;
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
 * Tests for the check-violation alert flow inside DistributedTaskServiceImpl.
 * Verifies that evaluateAndFireCheckAlerts fires (or suppresses) alerts
 * based on check type, alertValue/operator, and severity configuration.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CheckViolationAlertTest {

    @Mock CustomerRepository customerRepository;
    @Mock TaskExecutionSlotRepository taskExecutionSlotRepository;
    @Mock TaskDistributionMapper taskDistributionMapper;
    @Mock AttendanceFeignClient attendanceClient;
    @Mock TaskPresenter taskPresenter;
    @Mock TaskExecutionPresenter taskExecutionPresenter;
    @Mock TriggerRepository triggerRepository;
    @Mock CrmTriggerLogService crmTriggerLogService;
    @Mock C2AlertEventService c2AlertEventService;

    DistributedTaskServiceImpl service;

    Customer customer;
    TaskExecutionSlot slot;
    CrmTriggerLog triggerLog;

    @BeforeEach
    void setUp() {
        service = new DistributedTaskServiceImpl(
                customerRepository, taskExecutionSlotRepository,
                taskDistributionMapper, attendanceClient, taskPresenter, taskExecutionPresenter,
                triggerRepository, crmTriggerLogService, c2AlertEventService
        );

        // Location with coordinates
        Location location = Location.builder()
                .id(10L)
                .longitude(new BigDecimal("55.123"))
                .latitude(new BigDecimal("25.456"))
                .build();

        // Mock deep chain: serviceTime -> siteDistribution -> site -> id
        LKCustomerContractOperationService serviceTime = mock(LKCustomerContractOperationService.class);
        SiteDistribution siteDistribution = mock(SiteDistribution.class);
        CustomerSite site = mock(CustomerSite.class);
        when(site.getId()).thenReturn(100L);
        when(siteDistribution.getSite()).thenReturn(site);
        when(serviceTime.getSiteDistribution()).thenReturn(siteDistribution);

        PatrolTaskDistribution ptd = PatrolTaskDistribution.builder()
                .location(location)
                .serviceTime(serviceTime)
                .build();

        // New-path distribution: task=null, taskDefinitionId=42L, PATROL type
        TaskDistribution taskDistribution = TaskDistribution.builder()
                .taskDefinitionId(42L)
                .distributionType(DistributionType.PATROL)
                .build();
        taskDistribution.setPatrolTaskDistribution(ptd);

        customer = new Customer();
        customer.setId(1L);

        OffsetDateTime now = OffsetDateTime.now();
        slot = TaskExecutionSlot.builder()
                .id(1L)
                .status(TaskDistributionStatus.CURRENT)
                .startDateTime(now.minusHours(1))
                .endDateTime(now.plusHours(1))
                .taskDistribution(taskDistribution)
                .customer(customer)
                .build();

        // Common stubs
        when(attendanceClient.checkInData()).thenReturn(
                CheckInData.builder().customerId(1L).workforceId(5L).build());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(taskExecutionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));

        when(taskExecutionPresenter.createTaskExecution(any()))
                .thenReturn(TaskExecutionPayload.builder().id(100L).build());
        when(taskExecutionPresenter.submitTaskCheckExecution(any()))
                .thenReturn(TaskCheckExecutionPayload.builder().id(200L).build());
        when(taskExecutionPresenter.createTaskCheckComparison(any()))
                .thenReturn(mock(TaskCheckComparisonPayload.class));

        // Trigger for PATROL_TASK_DEVIATION (id=2)
        Trigger trigger = new Trigger();
        trigger.setId(2L);
        trigger.setCode("PATROL_TASK_Deviation");
        when(triggerRepository.findById(2L)).thenReturn(Optional.of(trigger));

        triggerLog = new CrmTriggerLog();
        triggerLog.setId(99L);
        when(crmTriggerLogService.addNewCrmTriggerLog(any())).thenReturn(triggerLog);
    }

    // ─── LIST check ──────────────────────────────────────────────────────────────

    @Test
    void listCheck_submittedValueMatchesAlertValue_alertFires() {
        // alertValue="Fail", submitted "Fail" → violated → alert fires
        ListCheckValue settings = new ListCheckValue(List.of("Pass", "Fail"), "Fail");
        TaskCheckDefinitionPayload checkDef = checkDef("Security Status", "HIGH", "LIST", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckListDTO dto = new TaskCheckListDTO();
        dto.setListItems(List.of("Fail"));

        service.executeTask(request(dto), null);

        verify(c2AlertEventService).sendNewC2AlertEventWithOverrideSeverity(
                eq(triggerLog), eq(Severity.HIGH), eq(6L));
    }

    @Test
    void listCheck_submittedValueDoesNotMatchAlertValue_noAlert() {
        // alertValue="Fail", submitted "Pass" → not violated → no alert
        ListCheckValue settings = new ListCheckValue(List.of("Pass", "Fail"), "Fail");
        TaskCheckDefinitionPayload checkDef = checkDef("Security Status", "HIGH", "LIST", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckListDTO dto = new TaskCheckListDTO();
        dto.setListItems(List.of("Pass"));

        service.executeTask(request(dto), null);

        verify(c2AlertEventService, never()).sendNewC2AlertEventWithOverrideSeverity(any(), any(), any());
    }

    @Test
    void listCheck_noAlertValue_noAlert() {
        // alertValue=null → never triggers regardless of submitted value
        ListCheckValue settings = new ListCheckValue(List.of("Pass", "Fail"), null);
        TaskCheckDefinitionPayload checkDef = checkDef("Security Status", "HIGH", "LIST", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckListDTO dto = new TaskCheckListDTO();
        dto.setListItems(List.of("Fail"));

        service.executeTask(request(dto), null);

        verify(c2AlertEventService, never()).sendNewC2AlertEventWithOverrideSeverity(any(), any(), any());
    }

    @Test
    void listCheck_descriptionIsTaskNameDashCheckName() {
        // For LIST violations the description is "taskName - checkName"
        ListCheckValue settings = new ListCheckValue(List.of("Pass", "Fail"), "Fail");
        TaskCheckDefinitionPayload checkDef = checkDef("Perimeter Status", "CRITICAL", "LIST", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(
                TaskDefinitionPayload.builder().id(42L).name("Evening Rounds").checks(List.of(checkDef)).build());

        TaskCheckListDTO dto = new TaskCheckListDTO();
        dto.setListItems(List.of("Fail"));

        service.executeTask(request(dto), null);

        ArgumentCaptor<TriggerEventDto> captor = ArgumentCaptor.forClass(TriggerEventDto.class);
        verify(crmTriggerLogService).addNewCrmTriggerLog(captor.capture());
        assertThat(captor.getValue().getDescription()).isEqualTo("Evening Rounds - Perimeter Status");
    }

    // ─── NUMBER check ─────────────────────────────────────────────────────────────

    @Test
    void numberCheck_violatesOperator_alertFires() {
        // operator="gte", threshold=10, submitted=5 → 5 >= 10 is false → violated
        NumberCheckValue settings = new NumberCheckValue("items", "gte", 10);
        TaskCheckDefinitionPayload checkDef = checkDef("Item Count", "HIGH", "NUMBER", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
        dto.setOperator("gte");
        dto.setValue(5);

        service.executeTask(request(dto), null);

        verify(c2AlertEventService).sendNewC2AlertEventWithOverrideSeverity(
                eq(triggerLog), eq(Severity.HIGH), eq(6L));
    }

    @Test
    void numberCheck_satisfiesOperator_noAlert() {
        // operator="gte", threshold=10, submitted=15 → 15 >= 10 is true → not violated
        NumberCheckValue settings = new NumberCheckValue("items", "gte", 10);
        TaskCheckDefinitionPayload checkDef = checkDef("Item Count", "HIGH", "NUMBER", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
        dto.setOperator("gte");
        dto.setValue(15);

        service.executeTask(request(dto), null);

        verify(c2AlertEventService, never()).sendNewC2AlertEventWithOverrideSeverity(any(), any(), any());
    }

    @Test
    void numberCheck_descriptionIsTaskNameDashCheckName() {
        // For NUMBER violations the description is "taskName - checkName"
        NumberCheckValue settings = new NumberCheckValue("items", "gte", 10);
        TaskCheckDefinitionPayload checkDef = checkDef("Item Count", "HIGH", "NUMBER", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(
                TaskDefinitionPayload.builder().id(42L).name("Stock Audit Task").checks(List.of(checkDef)).build());

        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
        dto.setOperator("gte");
        dto.setValue(5);

        service.executeTask(request(dto), null);

        ArgumentCaptor<TriggerEventDto> captor = ArgumentCaptor.forClass(TriggerEventDto.class);
        verify(crmTriggerLogService).addNewCrmTriggerLog(captor.capture());
        assertThat(captor.getValue().getDescription()).isEqualTo("Stock Audit Task - Item Count");
    }

    // ─── DECIMAL check ────────────────────────────────────────────────────────────

    @Test
    void decimalCheck_violatesOperator_alertFires() {
        // operator="lte", threshold=75.5, submitted=80.0 → 80.0 <= 75.5 is false → violated
        DecimalCheckValue settings = new DecimalCheckValue("kg", "lte", 75.5);
        TaskCheckDefinitionPayload checkDef = checkDef("Weight Check", "LOW", "DECIMAL", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckDecimalDTO dto = new TaskCheckDecimalDTO();
        dto.setOperator("lte");
        dto.setValue(80.0);

        service.executeTask(request(dto), null);

        verify(c2AlertEventService).sendNewC2AlertEventWithOverrideSeverity(
                eq(triggerLog), eq(Severity.LOW), eq(6L));
    }

    @Test
    void decimalCheck_satisfiesOperator_noAlert() {
        // operator="lte", threshold=75.5, submitted=70.0 → 70.0 <= 75.5 is true → not violated
        DecimalCheckValue settings = new DecimalCheckValue("kg", "lte", 75.5);
        TaskCheckDefinitionPayload checkDef = checkDef("Weight Check", "LOW", "DECIMAL", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckDecimalDTO dto = new TaskCheckDecimalDTO();
        dto.setOperator("lte");
        dto.setValue(70.0);

        service.executeTask(request(dto), null);

        verify(c2AlertEventService, never()).sendNewC2AlertEventWithOverrideSeverity(any(), any(), any());
    }

    // ─── Severity and AlertTrigger ID ─────────────────────────────────────────────

    @Test
    void checkWithoutSeverity_noAlert() {
        // severity=null → check is excluded from alerting entirely
        NumberCheckValue settings = new NumberCheckValue("items", "gte", 10);
        TaskCheckDefinitionPayload checkDef = checkDef("Item Count", null, "NUMBER", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
        dto.setOperator("gte");
        dto.setValue(0);   // clearly violated: 0 >= 10 is false

        service.executeTask(request(dto), null);

        verify(c2AlertEventService, never()).sendNewC2AlertEventWithOverrideSeverity(any(), any(), any());
    }

    @Test
    void alertFired_withCorrectSeverityFromCheckDef() {
        NumberCheckValue settings = new NumberCheckValue("items", "gte", 10);
        TaskCheckDefinitionPayload checkDef = checkDef("Item Count", "CRITICAL", "NUMBER", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
        dto.setOperator("gte");
        dto.setValue(1);

        service.executeTask(request(dto), null);

        ArgumentCaptor<Severity> severityCaptor = ArgumentCaptor.forClass(Severity.class);
        verify(c2AlertEventService).sendNewC2AlertEventWithOverrideSeverity(
                any(), severityCaptor.capture(), any());
        assertThat(severityCaptor.getValue()).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void alertFired_alwaysWithAlertTriggerId6() {
        NumberCheckValue settings = new NumberCheckValue("items", "gte", 10);
        TaskCheckDefinitionPayload checkDef = checkDef("Item Count", "HIGH", "NUMBER", settings);
        when(taskPresenter.getTaskDefinition(42L)).thenReturn(taskDef(checkDef));

        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
        dto.setOperator("gte");
        dto.setValue(1);

        service.executeTask(request(dto), null);

        ArgumentCaptor<Long> alertIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(c2AlertEventService).sendNewC2AlertEventWithOverrideSeverity(
                any(), any(), alertIdCaptor.capture());
        assertThat(alertIdCaptor.getValue()).isEqualTo(6L);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private ExecuteDistributedTaskRequest request(
            com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDTO... checks) {
        return ExecuteDistributedTaskRequest.builder()
                .executionSlotId(1L)
                .checks(List.of(checks))
                .build();
    }

    private TaskCheckDefinitionPayload checkDef(
            String name, String severity, String checkType,
            com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue settings) {
        return TaskCheckDefinitionPayload.builder()
                .id(10L).name(name).severity(severity).checkType(checkType)
                .checkSettings(settings).build();
    }

    private TaskDefinitionPayload taskDef(TaskCheckDefinitionPayload... checks) {
        return TaskDefinitionPayload.builder()
                .id(42L).name("Evening Check Task").checks(List.of(checks)).build();
    }
}
