package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.OperationSiteCameraRequestDto;
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
@RequestMapping(path = "/operation-site-cameras")
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

    @Operation(summary = "Get all cameras for a specific operation site with full details (camera + vendor info)")
    @GetMapping(path = "/{operationSiteId}")
    ApiResponse<List<OperationSiteCameraResponseDto>> getCamerasByOperationSite(
            @PathVariable("operationSiteId") Long operationSiteId
    ) {
        return ApiResponse.ok(operationSiteCameraService.getCamerasByOperationSiteId(operationSiteId));
    }
}

