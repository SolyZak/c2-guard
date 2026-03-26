package com.eden.eden_crm_sec_crm_back.service.impl;

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
        // Validate camera exists
        Camera camera = cameraRepository.findById(dto.cameraId())
                .orElseThrow(() -> new BusinessException("Camera not found", HttpStatus.NOT_FOUND));

        // Validate operation site exists
        CustomerSite operationSite = customerSiteRepository.findById(dto.operationSiteId())
                .orElseThrow(() -> new BusinessException("Operation site not found", HttpStatus.NOT_FOUND));

        OperationSiteCamera operationSiteCamera = mapper.requestToOperationSiteCamera(dto);
        operationSiteCamera.setCamera(camera);
        operationSiteCamera.setOperationSite(operationSite);

        try {
            operationSiteCamera = repository.save(operationSiteCamera);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Camera is already assigned to this operation site", HttpStatus.CONFLICT);
        }

        return mapper.operationSiteCameraToResponse(operationSiteCamera);
    }

    @Override
    public List<OperationSiteCameraResponseDto> getCamerasByOperationSiteId(Long operationSiteId) {
        // Validate operation site exists
        customerSiteRepository.findById(operationSiteId)
                .orElseThrow(() -> new BusinessException("Operation site not found", HttpStatus.NOT_FOUND));

        return repository.findByOperationSiteIdWithCameraAndVendor(operationSiteId)
                .stream()
                .map(mapper::operationSiteCameraToResponse)
                .toList();
    }
}

