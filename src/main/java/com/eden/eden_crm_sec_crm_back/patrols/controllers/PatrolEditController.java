package com.eden.eden_crm_sec_crm_back.patrols.controllers;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.edit.PatrolEditSaveResponse;
import com.eden.eden_crm_sec_crm_back.patrols.services.PatrolEditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * US1: edit patrol definition. Lock-guarded per patrol. Copy-on-edit creates a
 * new patrol version on save and propagates to all services using the patrol.
 */
@RestController
@RequestMapping("/customer/patrols")
@RequiredArgsConstructor
@Tag(name = "Patrol edit (US1)", description = "Edit patrol definition with versioning")
public class PatrolEditController {

    private final PatrolEditService patrolEditService;

    @Operation(summary = "Load patrol definition for editing")
    @GetMapping("/{patrolId}/edit")
    public ApiResponse<PatrolEditResponse> getForEdit(@PathVariable Long patrolId) {
        return ApiResponse.ok(patrolEditService.getForEdit(patrolId));
    }

    @Operation(summary = "Save edited patrol (creates a new version if tasks/frequency/locations changed)")
    @PatchMapping("/{patrolId}")
    public ApiResponse<PatrolEditSaveResponse> save(
            @PathVariable Long patrolId,
            @Valid @RequestBody PatrolEditRequest request
    ) {
        return ApiResponse.ok(patrolEditService.save(patrolId, request));
    }
}
