package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.CreateCameraRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.CameraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/cameras")
@Tag(
        name = "Cameras APIs",
        description = "APIs for managing cameras"
)
@RequiredArgsConstructor
public class CamerasController {
    private final CameraService cameraService;

    @Operation(summary = "Create a new camera")
    @PostMapping
    ApiResponse<CameraResponseDto> createCamera(@Valid @RequestBody CreateCameraRequest dto) {
        return ApiResponse.ok(cameraService.create(dto));
    }

    @Operation(summary = "Get all cameras without pagination")
    @GetMapping
    ApiResponse<List<CameraResponseDto>> getAllCameras() {
        return ApiResponse.ok(cameraService.getAll());
    }

    @Operation(summary = "Get all cameras with pagination")
    @GetMapping(path = "paginated")
    ApiResponse<PaginateResponse<CameraResponseDto>> getPaginatedCameras(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size
    ) {
        return ApiResponse.ok(cameraService.getPaginated(page, size));
    }
}

