package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.controller.WorkforceController;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDecimalDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckListDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckNumberDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckTextDTO;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.DecimalCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.ListCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.NumberCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TextCheckValue;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskCheckDefinitionPayload;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads.TaskDefinitionPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration tests for getTaskById flow in WorkforceController.
 */
@ExtendWith(MockitoExtension.class)
class WorkforceControllerDualModeIntegrationTest {

    @Mock private WorkforceService workforceService;
    @Mock private CustomerSiteService customerSiteService;
    @Mock private TaskPresenter taskPresenter;

    private WorkforceController controller;

    private static final Long TASK_ID      = 5L;
    private static final Long LOCATION_ID  = 78L;
    private static final Long CHECK_DEF_ID = 10L;
    private static final String REF_IMAGE_URL = "https://storage.example.com/o/images/ref/10.jpg";

    @BeforeEach
    void setUp() {
        controller = new WorkforceController(workforceService, customerSiteService, taskPresenter);
    }

    // ─── WITHOUT locationId (backward-compatible) ─────────────────────────────

    @Test
    @DisplayName("[NO LOCATION] taskPresenter.getTaskDefinition called when locationId is null")
    void getTaskById_withoutLocationId_callsGetTaskDefinition() {
        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
                .id(TASK_ID)
                .name("Check Perimeter")
                .checks(List.of(
                        TaskCheckDefinitionPayload.builder()
                                .id(CHECK_DEF_ID)
                                .name("Describe finding")
                                .checkType("TEXT")
                                .checkSettings(new TextCheckValue("Enter notes here"))
                                .hasEvidence(true)
                                .hasComment(false)
                                .build()
                ))
                .build();

        when(taskPresenter.getTaskDefinition(TASK_ID)).thenReturn(payload);

        ApiResponse<TaskCheckDto> response = controller.getTaskById(TASK_ID, null);

        verify(taskPresenter).getTaskDefinition(TASK_ID);
        verify(taskPresenter, never()).getTaskDefinitionWithReferenceImages(any(), any());

        assertThat(response.getPayload()).isNotNull();
        assertThat(response.getPayload().taskName()).isEqualTo("Check Perimeter");
        assertThat(response.getPayload().checks()).hasSize(1);

        TaskCheckTextDTO check = (TaskCheckTextDTO) response.getPayload().checks().get(0);
        assertThat(check.getId()).isEqualTo(CHECK_DEF_ID);
        assertThat(check.getName()).isEqualTo("Describe finding");
        assertThat(check.getEvidence()).isTrue();
        assertThat(check.getCommentCheck()).isFalse();
        assertThat(check.getNotes()).isEqualTo("Enter notes here");
        assertThat(check.getReferenceImageUrl()).isNull();
    }

    // ─── WITH locationId (reference images enriched) ──────────────────────────

    @Test
    @DisplayName("[WITH LOCATION] taskPresenter.getTaskDefinitionWithReferenceImages called when locationId is provided")
    void getTaskById_withLocationId_callsGetTaskDefinitionWithReferenceImages() {
        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
                .id(TASK_ID)
                .name("Check Perimeter")
                .checks(List.of(
                        TaskCheckDefinitionPayload.builder()
                                .id(CHECK_DEF_ID)
                                .name("Describe finding")
                                .checkType("TEXT")
                                .checkSettings(new TextCheckValue("Enter notes here"))
                                .hasEvidence(true)
                                .hasComment(false)
                                .referenceImageUrl(REF_IMAGE_URL)
                                .build()
                ))
                .build();

        when(taskPresenter.getTaskDefinitionWithReferenceImages(TASK_ID, LOCATION_ID))
                .thenReturn(payload);

        ApiResponse<TaskCheckDto> response = controller.getTaskById(TASK_ID, LOCATION_ID);

        verify(taskPresenter).getTaskDefinitionWithReferenceImages(TASK_ID, LOCATION_ID);
        verify(taskPresenter, never()).getTaskDefinition(any());

        assertThat(response.getPayload()).isNotNull();
        assertThat(response.getPayload().taskName()).isEqualTo("Check Perimeter");
        assertThat(response.getPayload().checks()).hasSize(1);

        TaskCheckTextDTO check = (TaskCheckTextDTO) response.getPayload().checks().get(0);
        assertThat(check.getId()).isEqualTo(CHECK_DEF_ID);
        assertThat(check.getReferenceImageUrl()).isEqualTo(REF_IMAGE_URL);
    }

    // ─── All 4 check types (without location — backward compat) ───────────────

    @Test
    @DisplayName("[NO LOCATION] All 4 check types convert correctly, referenceImageUrl is null")
    void getTaskById_allCheckTypes_withoutLocation_convertCorrectly() {
        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
                .id(TASK_ID)
                .name("Multi-Check Task")
                .checks(List.of(
                        TaskCheckDefinitionPayload.builder()
                                .id(1L).name("Text check").checkType("TEXT")
                                .checkSettings(new TextCheckValue("default note"))
                                .hasEvidence(true).hasComment(false).build(),

                        TaskCheckDefinitionPayload.builder()
                                .id(2L).name("Number check").checkType("NUMBER")
                                .checkSettings(new NumberCheckValue("kg", "gte", 10))
                                .hasEvidence(false).hasComment(true).build(),

                        TaskCheckDefinitionPayload.builder()
                                .id(3L).name("Decimal check").checkType("DECIMAL")
                                .checkSettings(new DecimalCheckValue("m", "lte", 5.5))
                                .hasEvidence(false).hasComment(false).build(),

                        TaskCheckDefinitionPayload.builder()
                                .id(4L).name("List check").checkType("LIST")
                                .checkSettings(new ListCheckValue(List.of("Pass", "Fail"), null))
                                .hasEvidence(true).hasComment(true).build()
                ))
                .build();

        when(taskPresenter.getTaskDefinition(TASK_ID)).thenReturn(payload);

        ApiResponse<TaskCheckDto> response = controller.getTaskById(TASK_ID, null);
        var checks = response.getPayload().checks();
        assertThat(checks).hasSize(4);

        TaskCheckTextDTO textCheck = (TaskCheckTextDTO) checks.get(0);
        assertThat(textCheck.getNotes()).isEqualTo("default note");
        assertThat(textCheck.getEvidence()).isTrue();
        assertThat(textCheck.getCommentCheck()).isFalse();
        assertThat(textCheck.getReferenceImageUrl()).isNull();

        TaskCheckNumberDTO numCheck = (TaskCheckNumberDTO) checks.get(1);
        assertThat(numCheck.getUnit()).isEqualTo("kg");
        assertThat(numCheck.getOperator()).isEqualTo("gte");
        assertThat(numCheck.getValue()).isEqualTo(10);
        assertThat(numCheck.getCommentCheck()).isTrue();
        assertThat(numCheck.getReferenceImageUrl()).isNull();

        TaskCheckDecimalDTO decCheck = (TaskCheckDecimalDTO) checks.get(2);
        assertThat(decCheck.getUnit()).isEqualTo("m");
        assertThat(decCheck.getOperator()).isEqualTo("lte");
        assertThat(decCheck.getValue()).isEqualTo(5.5);
        assertThat(decCheck.getReferenceImageUrl()).isNull();

        TaskCheckListDTO listCheck = (TaskCheckListDTO) checks.get(3);
        assertThat(listCheck.getListItems()).containsExactly("Pass", "Fail");
        assertThat(listCheck.getEvidence()).isTrue();
        assertThat(listCheck.getCommentCheck()).isTrue();
        assertThat(listCheck.getReferenceImageUrl()).isNull();
    }

    // ─── All 4 check types (with location — reference images enriched) ────────

    @Test
    @DisplayName("[WITH LOCATION] All 4 check types convert correctly with referenceImageUrl set")
    void getTaskById_allCheckTypes_withLocation_convertCorrectly() {
        String baseUrl = "https://storage.example.com/o/";

        TaskDefinitionPayload payload = TaskDefinitionPayload.builder()
                .id(TASK_ID)
                .name("Multi-Check Task")
                .checks(List.of(
                        TaskCheckDefinitionPayload.builder()
                                .id(1L).name("Text check").checkType("TEXT")
                                .checkSettings(new TextCheckValue("default note"))
                                .hasEvidence(true).hasComment(false)
                                .referenceImageUrl(baseUrl + "images/ref/1.jpg")
                                .build(),

                        TaskCheckDefinitionPayload.builder()
                                .id(2L).name("Number check").checkType("NUMBER")
                                .checkSettings(new NumberCheckValue("kg", "gte", 10))
                                .hasEvidence(false).hasComment(true)
                                .referenceImageUrl(baseUrl + "images/ref/2.jpg")
                                .build(),

                        TaskCheckDefinitionPayload.builder()
                                .id(3L).name("Decimal check").checkType("DECIMAL")
                                .checkSettings(new DecimalCheckValue("m", "lte", 5.5))
                                .hasEvidence(false).hasComment(false)
                                .referenceImageUrl(null)  // no image uploaded for this check
                                .build(),

                        TaskCheckDefinitionPayload.builder()
                                .id(4L).name("List check").checkType("LIST")
                                .checkSettings(new ListCheckValue(List.of("Pass", "Fail"), null))
                                .hasEvidence(true).hasComment(true)
                                .referenceImageUrl(baseUrl + "images/ref/4.jpg")
                                .build()
                ))
                .build();

        when(taskPresenter.getTaskDefinitionWithReferenceImages(TASK_ID, LOCATION_ID))
                .thenReturn(payload);

        ApiResponse<TaskCheckDto> response = controller.getTaskById(TASK_ID, LOCATION_ID);
        var checks = response.getPayload().checks();
        assertThat(checks).hasSize(4);

        TaskCheckTextDTO textCheck = (TaskCheckTextDTO) checks.get(0);
        assertThat(textCheck.getNotes()).isEqualTo("default note");
        assertThat(textCheck.getReferenceImageUrl()).isEqualTo(baseUrl + "images/ref/1.jpg");

        TaskCheckNumberDTO numCheck = (TaskCheckNumberDTO) checks.get(1);
        assertThat(numCheck.getUnit()).isEqualTo("kg");
        assertThat(numCheck.getReferenceImageUrl()).isEqualTo(baseUrl + "images/ref/2.jpg");

        TaskCheckDecimalDTO decCheck = (TaskCheckDecimalDTO) checks.get(2);
        assertThat(decCheck.getUnit()).isEqualTo("m");
        assertThat(decCheck.getReferenceImageUrl()).isNull();  // no image

        TaskCheckListDTO listCheck = (TaskCheckListDTO) checks.get(3);
        assertThat(listCheck.getListItems()).containsExactly("Pass", "Fail");
        assertThat(listCheck.getReferenceImageUrl()).isEqualTo(baseUrl + "images/ref/4.jpg");
    }
}