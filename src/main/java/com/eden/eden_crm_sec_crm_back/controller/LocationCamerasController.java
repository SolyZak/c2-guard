package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.BulkLocationCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.LocationCameraResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.LocationCameraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/location-cameras")
@Tag(
        name = "Location Cameras APIs",
        description = "APIs for managing camera assignments to locations"
)
@RequiredArgsConstructor
public class LocationCamerasController {

    private final LocationCameraService locationCameraService;

    @Operation(summary = "Assign a camera to a location")
    @PostMapping
    ApiResponse<LocationCameraResponseDto> assignCameraToLocation(
            @Valid @RequestBody LocationCameraRequestDto dto
    ) {
        return ApiResponse.ok(locationCameraService.assignCameraToLocation(dto));
    }

    @Operation(summary = "Assign multiple cameras to a location")
    @PostMapping(path = "/bulk")
    ApiResponse<List<LocationCameraResponseDto>> bulkAssignCamerasToLocation(
            @Valid @RequestBody BulkLocationCameraRequestDto dto
    ) {
        return ApiResponse.ok(locationCameraService.bulkAssignCamerasToLocation(dto));
    }

    @Operation(summary = "Get all cameras for a specific location with full details")
    @GetMapping(path = "/{locationId}")
    ApiResponse<List<LocationCameraResponseDto>> getCamerasByLocation(
            @PathVariable("locationId") Long locationId
    ) {
        return ApiResponse.ok(locationCameraService.getCamerasByLocationId(locationId));
    }
}