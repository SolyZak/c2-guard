package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.BulkOperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.OperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraAssignmentResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.OperationSiteCameraResponseDto;

import java.util.List;

public interface OperationSiteCameraService {

    OperationSiteCameraResponseDto assignCameraToOperationSite(OperationSiteCameraRequestDto dto);

    List<OperationSiteCameraResponseDto> bulkAssignCamerasToOperationSite(BulkOperationSiteCameraRequestDto dto);

    List<OperationSiteCameraResponseDto> getCamerasByOperationSiteId(Long operationSiteId);

    List<CameraAssignmentResponseDto> getAvailableCamerasForOperationSite(Long operationSiteId);
}