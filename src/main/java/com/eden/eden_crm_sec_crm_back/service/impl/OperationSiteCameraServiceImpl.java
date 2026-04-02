package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.BulkOperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.OperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraAssignmentResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.OperationSiteCameraResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.CameraMapper;
import com.eden.eden_crm_sec_crm_back.mapper.OperationSiteCameraMapper;
import com.eden.eden_crm_sec_crm_back.models.Camera;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.OperationSiteCamera;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.repository.CameraRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.OperationSiteCameraRepository;
import com.eden.eden_crm_sec_crm_back.service.OperationSiteCameraService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OperationSiteCameraServiceImpl implements OperationSiteCameraService {

    private final OperationSiteCameraRepository repository;
    private final OperationSiteCameraMapper mapper;
    private final CameraMapper cameraMapper;
    private final CameraRepository cameraRepository;
    private final CustomerSiteRepository customerSiteRepository;
    private final Utils utils;

    @Transactional
    @Override
    public OperationSiteCameraResponseDto assignCameraToOperationSite(OperationSiteCameraRequestDto dto) {
        Camera camera = cameraRepository.findById(dto.cameraId())
                .orElseThrow(() -> new BusinessException("Camera not found", HttpStatus.NOT_FOUND));

        CustomerSite operationSite = customerSiteRepository.findById(dto.operationSiteId())
                .orElseThrow(() -> new BusinessException("Operation site not found", HttpStatus.NOT_FOUND));

        OperationSiteCamera operationSiteCamera = OperationSiteCamera.builder()
                .camera(camera)
                .operationSite(operationSite)
                .build();

        try {
            operationSiteCamera = repository.save(operationSiteCamera);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Camera is already assigned to this operation site", HttpStatus.CONFLICT);
        }

        return mapper.operationSiteCameraToResponse(operationSiteCamera);
    }

    @Transactional
    @Override
    public List<OperationSiteCameraResponseDto> bulkAssignCamerasToOperationSite(BulkOperationSiteCameraRequestDto dto) {
        CustomerSite operationSite = customerSiteRepository.findById(dto.operationSiteId())
                .orElseThrow(() -> new BusinessException("Operation site not found", HttpStatus.NOT_FOUND));

        List<Camera> cameras = cameraRepository.findAllById(dto.cameraIds());

        if (cameras.size() != dto.cameraIds().size()) {
            List<Long> foundIds = cameras.stream().map(Camera::getId).toList();
            List<Long> missingIds = dto.cameraIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new BusinessException(
                    "Cameras not found with IDs: " + missingIds,
                    HttpStatus.NOT_FOUND
            );
        }

        List<OperationSiteCamera> operationSiteCameras = new ArrayList<>();
        for (Camera camera : cameras) {
            OperationSiteCamera osc = OperationSiteCamera.builder()
                    .camera(camera)
                    .operationSite(operationSite)
                    .build();
            operationSiteCameras.add(osc);
        }

        try {
            operationSiteCameras = repository.saveAll(operationSiteCameras);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(
                    "One or more cameras are already assigned to this operation site",
                    HttpStatus.CONFLICT
            );
        }

        return operationSiteCameras.stream()
                .map(mapper::operationSiteCameraToResponse)
                .toList();
    }

    @Override
    public List<OperationSiteCameraResponseDto> getCamerasByOperationSiteId(Long operationSiteId) {
        customerSiteRepository.findById(operationSiteId)
                .orElseThrow(() -> new BusinessException("Operation site not found", HttpStatus.NOT_FOUND));

        return repository.findByOperationSiteIdWithCameraAndVendor(operationSiteId)
                .stream()
                .map(mapper::operationSiteCameraToResponse)
                .toList();
    }

    @Override
    public List<CameraAssignmentResponseDto> getAvailableCamerasForOperationSite(Long operationSiteId) {
        // Validate operation site exists
        customerSiteRepository.findById(operationSiteId)
                .orElseThrow(() -> new BusinessException("Operation site not found", HttpStatus.NOT_FOUND));

        // Resolve customer from token
        UserData userData = utils.getLoggedInUser();
        Long customerId = userData.getCustomerId();

        // Get all cameras belonging to this customer
        List<Camera> allCameras = cameraRepository.findByCustomerIdWithDetails(customerId);

        // Get camera IDs already assigned to this operation site
        Set<Long> assignedCameraIds = new HashSet<>(
                repository.findCameraIdsByOperationSiteId(operationSiteId)
        );

        // Map with assigned boolean
        return allCameras.stream()
                .map(camera -> {
                    CameraAssignmentResponseDto dto = cameraMapper.cameraToAssignmentResponse(camera);
                    dto.setAssigned(assignedCameraIds.contains(camera.getId()));
                    return dto;
                })
                .toList();
    }
}