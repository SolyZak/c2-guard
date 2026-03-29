package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.BulkOperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.OperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.OperationSiteCameraResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.OperationSiteCameraMapper;
import com.eden.eden_crm_sec_crm_back.models.Camera;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.OperationSiteCamera;
import com.eden.eden_crm_sec_crm_back.repository.CameraRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.OperationSiteCameraRepository;
import com.eden.eden_crm_sec_crm_back.service.OperationSiteCameraService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationSiteCameraServiceImpl implements OperationSiteCameraService {

    private final OperationSiteCameraRepository repository;
    private final OperationSiteCameraMapper mapper;
    private final CameraRepository cameraRepository;
    private final CustomerSiteRepository customerSiteRepository;

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
        // Validate operation site exists
        CustomerSite operationSite = customerSiteRepository.findById(dto.operationSiteId())
                .orElseThrow(() -> new BusinessException("Operation site not found", HttpStatus.NOT_FOUND));

        // Fetch all cameras in one query
        List<Camera> cameras = cameraRepository.findAllById(dto.cameraIds());

        // Validate all camera IDs exist
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

        // Build all entities
        List<OperationSiteCamera> operationSiteCameras = new ArrayList<>();
        for (Camera camera : cameras) {
            OperationSiteCamera osc = OperationSiteCamera.builder()
                    .camera(camera)
                    .operationSite(operationSite)
                    .build();
            operationSiteCameras.add(osc);
        }

        // Save all in one batch
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
}