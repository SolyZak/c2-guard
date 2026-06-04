package com.eden.eden_crm_sec_crm_back.locks.controllers;

import com.eden.eden_crm_sec_crm_back.locks.dtos.AcquireLockResponse;
import com.eden.eden_crm_sec_crm_back.locks.dtos.LockStatusResponse;
import com.eden.eden_crm_sec_crm_back.locks.services.CustomerEditLockService;
import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Per-patrol edit lock. Keyed by patrolId. Guards both US1 (edit patrol) and
 * US2 (edit per-service distributions under that patrol). Multiple patrols
 * can be locked in parallel by different users.
 */
@RestController
@RequestMapping("/customer/patrol-lock")
@RequiredArgsConstructor
@Tag(name = "Patrol edit lock",
        description = "Per-patrol lock that guards the patrol-edit and distribution-edit screens.")
public class CustomerEditLockController {

    private final CustomerEditLockService lockService;

    @Operation(summary = "Acquire (or refresh) the lock for a patrol")
    @PostMapping("/{patrolId}/acquire")
    public ResponseEntity<ApiResponse<AcquireLockResponse>> acquire(@PathVariable Long patrolId) {
        AcquireLockResponse body = lockService.acquire(patrolId);
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @Operation(summary = "Read current lock state for a patrol")
    @GetMapping("/{patrolId}/status")
    public ResponseEntity<ApiResponse<LockStatusResponse>> status(@PathVariable Long patrolId) {
        LockStatusResponse body = lockService.status(patrolId);
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @Operation(summary = "Release the lock held by the caller for a patrol (idempotent)")
    @PostMapping("/{patrolId}/release")
    public ResponseEntity<Void> release(@PathVariable Long patrolId) {
        lockService.release(patrolId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
