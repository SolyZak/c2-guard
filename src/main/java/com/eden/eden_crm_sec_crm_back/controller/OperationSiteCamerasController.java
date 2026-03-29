package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.BulkOperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.OperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraAssignmentResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.OperationSiteCameraResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.OperationSiteCameraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/eden/operation-site-cameras")
@Tag(
        name = "Operation Site Cameras APIs",
        description = "APIs for managing camera assignments to operation sites"
)
@RequiredArgsConstructor
public class OperationSiteCamerasController {

    private final OperationSiteCameraService operationSiteCameraService;

    @Operation(summary = "Assign a camera to an operation site")
    @PostMapping
    ApiResponse<OperationSiteCameraResponseDto> assignCameraToOperationSite(
            @Valid @RequestBody OperationSiteCameraRequestDto dto
    ) {
        return ApiResponse.ok(operationSiteCameraService.assignCameraToOperationSite(dto));
    }

    @Operation(summary = "Assign multiple cameras to an operation site")
    @PostMapping(path = "/bulk")
    ApiResponse<List<OperationSiteCameraResponseDto>> bulkAssignCamerasToOperationSite(
            @Valid @RequestBody BulkOperationSiteCameraRequestDto dto
    ) {
        return ApiResponse.ok(operationSiteCameraService.bulkAssignCamerasToOperationSite(dto));
    }

    @Operation(summary = "Get all cameras assigned to a specific operation site")
    @GetMapping(path = "/{operationSiteId}")
    ApiResponse<List<OperationSiteCameraResponseDto>> getCamerasByOperationSite(
            @PathVariable("operationSiteId") Long operationSiteId
    ) {
        return ApiResponse.ok(operationSiteCameraService.getCamerasByOperationSiteId(operationSiteId));
    }

    @Operation(summary = "Get all customer cameras with assignment status for a specific operation site")
    @GetMapping(path = "/{operationSiteId}/available")
    ApiResponse<List<CameraAssignmentResponseDto>> getAvailableCamerasForOperationSite(
            @PathVariable("operationSiteId") Long operationSiteId
    ) {
        return ApiResponse.ok(operationSiteCameraService.getAvailableCamerasForOperationSite(operationSiteId));
    }
}