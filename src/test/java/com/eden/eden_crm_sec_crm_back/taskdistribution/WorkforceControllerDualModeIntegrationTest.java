package com.eden.eden_crm_sec_crm_back.taskdistribution;

import com.eden.eden_crm_sec_crm_back.controller.WorkforceController;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDecimalDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckListDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckNumberDTO;
import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckTextDTO;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
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
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration test for the dual-path getTaskById flow in WorkforceController.
 *
 * Covers:
 *  - [NEW PATH]         taskPresenter succeeds → response built from TaskDefinitionPayload
 *  - [COEXISTENCE PATH] taskPresenter throws → fallback to old taskService
 *  - [CONVERSION]       all 4 check types convert correctly from new payload to old DTO
 *
 * // [TASK-MIGRATION] NEW: covers WorkforceController dual-mode introduced in this session.
 * // CLEANUP: after Phase E, remove COEXISTENCE test and taskService fallback.
 */
@ExtendWith(MockitoExtension.class)
class WorkforceControllerDualModeIntegrationTest {

    @Mock private WorkforceService workforceService;
    @Mock private CustomerSiteService customerSiteService;
    // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────────
    @Mock private TaskService taskService;
    // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────────
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    @Mock private TaskPresenter taskPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

    private WorkforceController controller;

    private static final Long TASK_ID            = 5L;
    private static final Long CHECK_DEF_ID       = 10L;

    @BeforeEach
    void setUp() {
        controller = new WorkforceController(
            workforceService, customerSiteService, taskService, taskPresenter
        );
    }

    // ── Test 1: New path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("[NEW PATH] taskPresenter succeeds → TaskCheckDto built from TaskDefinitionPayload")
    void getTaskById_whenPresenterSucceeds_returnsConvertedPayload() {
        // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────
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

        ApiResponse<TaskCheckDto> response = controller.getTaskById(TASK_ID);

        assertThat(response.getPayload()).isNotNull();
        assertThat(response.getPayload().taskName()).isEqualTo("Check Perimeter");
        assertThat(response.getPayload().checks()).hasSize(1);

        TaskCheckTextDTO check = (TaskCheckTextDTO) response.getPayload().checks().get(0);
        assertThat(check.getId()).isEqualTo(CHECK_DEF_ID);
        assertThat(check.getName()).isEqualTo("Describe finding");
        assertThat(check.getEvidence()).isTrue();
        assertThat(check.getCommentCheck()).isFalse();
        assertThat(check.getNotes()).isEqualTo("Enter notes here");

        // Old service must NOT be called
        verifyNoInteractions(taskService);
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────
    }

    // ── Test 2: Coexistence path ───────────────────────────────────────────────────

    @Test
    @DisplayName("[COEXISTENCE PATH] taskPresenter throws → fallback to legacy taskService")
    void getTaskById_whenPresenterThrows_fallsBackToLegacyTaskService() {
        // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────────
        // CLEANUP: remove this test after Phase E.
        when(taskPresenter.getTaskDefinition(TASK_ID))
            .thenThrow(new BusinessException("not found", HttpStatus.NOT_FOUND));

        TaskCheckDto legacyDto = new TaskCheckDto("Guard Round", List.of());
        when(taskService.getTaskById(TASK_ID)).thenReturn(legacyDto);

        ApiResponse<TaskCheckDto> response = controller.getTaskById(TASK_ID);

        assertThat(response.getPayload().taskName()).isEqualTo("Guard Round");
        verify(taskService).getTaskById(TASK_ID);
        // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────────
    }

    // ── Test 3: All check type conversions ────────────────────────────────────────

    @Test
    @DisplayName("[NEW PATH CONVERSION] All 4 check types convert correctly from payload to legacy DTO")
    void getTaskById_allCheckTypes_convertCorrectly() {
        // ─── [TASK-MIGRATION] NEW: validates the toTaskCheckDTO conversion helper ─────
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

        ApiResponse<TaskCheckDto> response = controller.getTaskById(TASK_ID);
        var checks = response.getPayload().checks();
        assertThat(checks).hasSize(4);

        // TEXT
        TaskCheckTextDTO textCheck = (TaskCheckTextDTO) checks.get(0);
        assertThat(textCheck.getNotes()).isEqualTo("default note");
        assertThat(textCheck.getEvidence()).isTrue();
        assertThat(textCheck.getCommentCheck()).isFalse();

        // NUMBER
        TaskCheckNumberDTO numCheck = (TaskCheckNumberDTO) checks.get(1);
        assertThat(numCheck.getUnit()).isEqualTo("kg");
        assertThat(numCheck.getOperator()).isEqualTo("gte");
        assertThat(numCheck.getValue()).isEqualTo(10);
        assertThat(numCheck.getCommentCheck()).isTrue();

        // DECIMAL
        TaskCheckDecimalDTO decCheck = (TaskCheckDecimalDTO) checks.get(2);
        assertThat(decCheck.getUnit()).isEqualTo("m");
        assertThat(decCheck.getOperator()).isEqualTo("lte");
        assertThat(decCheck.getValue()).isEqualTo(5.5);

        // LIST
        TaskCheckListDTO listCheck = (TaskCheckListDTO) checks.get(3);
        assertThat(listCheck.getListItems()).containsExactly("Pass", "Fail");
        assertThat(listCheck.getEvidence()).isTrue();
        assertThat(listCheck.getCommentCheck()).isTrue();
        // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────
    }
}
