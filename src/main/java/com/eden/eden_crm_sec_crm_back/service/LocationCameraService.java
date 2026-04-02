package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.BulkLocationCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraAssignmentResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.LocationCameraResponseDto;

import java.util.List;

public interface LocationCameraService {

    LocationCameraResponseDto assignCameraToLocation(LocationCameraRequestDto dto);

    List<LocationCameraResponseDto> bulkAssignCamerasToLocation(BulkLocationCameraRequestDto dto);

    List<LocationCameraResponseDto> getCamerasByLocationId(Long locationId);

    List<CameraAssignmentResponseDto> getAvailableCamerasForLocation(Long locationId);
}