package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for per-check image upload index alignment in DistributedTaskServiceImpl.
 * Verifies that images[i] is used for checks[i], and that missing/empty images
 * do not trigger uploads.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExecuteTaskImageUploadTest {

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

    // Two check definitions with distinct IDs for index verification
    static final Long CHECK_DEF_ID_0 = 10L;
    static final Long CHECK_DEF_ID_1 = 11L;

    @BeforeEach
    void setUp() {
        service = new DistributedTaskServiceImpl(
                customerRepository, taskExecutionSlotRepository,
                taskDistributionMapper, attendanceClient, taskPresenter, taskExecutionPresenter,
                triggerRepository, crmTriggerLogService, c2AlertEventService
        );

        Location location = Location.builder()
                .id(10L)
                .longitude(new BigDecimal("55.0"))
                .latitude(new BigDecimal("25.0"))
                .build();

        LKCustomerContractOperationService serviceTime = mock(LKCustomerContractOperationService.class);
        SiteDistribution siteDistribution = mock(SiteDistribution.class);
        CustomerSite site = mock(CustomerSite.class);
        when(site.getId()).thenReturn(100L);
        when(siteDistribution.getSite()).thenReturn(site);
        when(serviceTime.getSiteDistribution()).thenReturn(siteDistribution);

        PatrolTaskDistribution ptd = PatrolTaskDistribution.builder()
                .location(location).serviceTime(serviceTime).build();

        TaskDistribution taskDistribution = TaskDistribution.builder()
                .taskDefinitionId(42L).distributionType(DistributionType.PATROL).build();
        taskDistribution.setPatrolTaskDistribution(ptd);

        Customer customer = new Customer();
        customer.setId(1L);

        OffsetDateTime now = OffsetDateTime.now();
        TaskExecutionSlot slot = TaskExecutionSlot.builder()
                .id(1L).status(TaskDistributionStatus.CURRENT)
                .startDateTime(now.minusHours(1)).endDateTime(now.plusHours(1))
                .taskDistribution(taskDistribution).customer(customer)
                .build();

        when(attendanceClient.checkInData())
                .thenReturn(CheckInData.builder().customerId(1L).workforceId(5L).build());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(taskExecutionSlotRepository.findById(1L)).thenReturn(Optional.of(slot));

        when(taskExecutionPresenter.createTaskExecution(any()))
                .thenReturn(TaskExecutionPayload.builder().id(100L).build());
        when(taskExecutionPresenter.submitTaskCheckExecution(any()))
                .thenReturn(TaskCheckExecutionPayload.builder().id(200L).build());
        when(taskExecutionPresenter.createTaskCheckComparison(any()))
                .thenReturn(mock(TaskCheckComparisonPayload.class));
        when(taskExecutionPresenter.uploadCheckExecutionImage(any(), any()))
                .thenReturn("uploaded/path.jpg");

        // evaluateAndFireCheckAlerts() always fetches PATROL_TASK_DEVIATION trigger (id=2)
        // even when no check has a severity configured.
        com.eden.eden_crm_sec_crm_back.entity.Trigger deviationTrigger = new com.eden.eden_crm_sec_crm_back.entity.Trigger();
        deviationTrigger.setId(2L);
        deviationTrigger.setCode("PATROL_TASK_Deviation");
        when(triggerRepository.findById(2L)).thenReturn(Optional.of(deviationTrigger));

        // Two LIST checks without alertValue (no violation → no alert code path needed)
        TaskCheckDefinitionPayload def0 = TaskCheckDefinitionPayload.builder()
                .id(CHECK_DEF_ID_0).name("Check A").checkType("LIST")
                .checkSettings(new ListCheckValue(List.of("Pass", "Fail"), null)).build();
        TaskCheckDefinitionPayload def1 = TaskCheckDefinitionPayload.builder()
                .id(CHECK_DEF_ID_1).name("Check B").checkType("NUMBER")
                .checkSettings(new NumberCheckValue("items", "gte", 5)).build();

        when(taskPresenter.getTaskDefinition(42L)).thenReturn(
                TaskDefinitionPayload.builder().id(42L).name("Task").checks(List.of(def0, def1)).build());
    }

    @Test
    void imageAtIndex0_uploadedForCheckDefAtIndex0() {
        MultipartFile img0 = file("img0.jpg");
        MultipartFile img1 = file("img1.jpg");

        service.executeTask(twoCheckRequest(), List.of(img0, img1));

        ArgumentCaptor<Long> checkDefIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(taskExecutionPresenter, times(2))
                .uploadCheckExecutionImage(checkDefIdCaptor.capture(), any());

        List<Long> capturedIds = checkDefIdCaptor.getAllValues();
        assertThat(capturedIds.get(0)).isEqualTo(CHECK_DEF_ID_0);
        assertThat(capturedIds.get(1)).isEqualTo(CHECK_DEF_ID_1);
    }

    @Test
    void nullImagesList_noUploadCalls() {
        service.executeTask(twoCheckRequest(), null);

        verify(taskExecutionPresenter, never()).uploadCheckExecutionImage(any(), any());
    }

    @Test
    void emptyImagesList_noUploadCalls() {
        service.executeTask(twoCheckRequest(), List.of());

        verify(taskExecutionPresenter, never()).uploadCheckExecutionImage(any(), any());
    }

    @Test
    void imageShorterThanChecks_onlyFirstImageUploaded() {
        // Only one image provided for two checks; second check gets no image
        MultipartFile img0 = file("only.jpg");

        service.executeTask(twoCheckRequest(), List.of(img0));

        verify(taskExecutionPresenter, times(1)).uploadCheckExecutionImage(any(), any());
    }

    @Test
    void emptyMultipartFile_noUploadForThatIndex() {
        // First file is empty (0 bytes) — should not trigger upload
        MultipartFile empty = new MockMultipartFile("images", "empty.jpg", "image/jpeg", new byte[0]);
        MultipartFile real  = file("real.jpg");

        service.executeTask(twoCheckRequest(), List.of(empty, real));

        // Only the second (real) file should trigger an upload
        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(taskExecutionPresenter, times(1))
                .uploadCheckExecutionImage(any(), fileCaptor.capture());
        assertThat(fileCaptor.getValue().getOriginalFilename()).isEqualTo("real.jpg");
    }

    @Test
    void nullEntryInImagesList_noUploadForThatIndex() {
        MultipartFile real = file("real.jpg");
        // null at index 0, real file at index 1
        List<MultipartFile> images = Arrays.asList(null, real);

        service.executeTask(twoCheckRequest(), images);

        ArgumentCaptor<Long> checkDefIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(taskExecutionPresenter, times(1))
                .uploadCheckExecutionImage(checkDefIdCaptor.capture(), any());
        // Only index 1 (CHECK_DEF_ID_1) should have been uploaded
        assertThat(checkDefIdCaptor.getValue()).isEqualTo(CHECK_DEF_ID_1);
    }

    @Test
    void uploadedPathPassedToSubmitExecution() {
        when(taskExecutionPresenter.uploadCheckExecutionImage(eq(CHECK_DEF_ID_0), any()))
                .thenReturn("task-execution-images/10/exec_10_123.jpg");

        MultipartFile img0 = file("img0.jpg");
        service.executeTask(twoCheckRequest(), List.of(img0));

        ArgumentCaptor<com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload>
                submitCaptor = ArgumentCaptor.forClass(
                        com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.SubmitTaskCheckExecutionPayload.class);
        verify(taskExecutionPresenter, atLeastOnce()).submitTaskCheckExecution(submitCaptor.capture());

        // First submit payload (for check at index 0) should carry the uploaded path
        assertThat(submitCaptor.getAllValues().get(0).getEvidenceImagePath())
                .isEqualTo("task-execution-images/10/exec_10_123.jpg");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private ExecuteDistributedTaskRequest twoCheckRequest() {
        TaskCheckListDTO list = new TaskCheckListDTO();
        list.setListItems(List.of("Pass"));

        TaskCheckNumberDTO number = new TaskCheckNumberDTO();
        number.setOperator("gte");
        number.setValue(10);

        return ExecuteDistributedTaskRequest.builder()
                .executionSlotId(1L)
                .checks(List.of(list, number))
                .build();
    }

    private MockMultipartFile file(String filename) {
        return new MockMultipartFile("images", filename, "image/jpeg",
                new byte[]{1, 2, 3});
    }
}
