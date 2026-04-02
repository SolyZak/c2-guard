package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.BulkLocationCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraAssignmentResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.LocationCameraResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.CameraMapper;
import com.eden.eden_crm_sec_crm_back.mapper.LocationCameraMapper;
import com.eden.eden_crm_sec_crm_back.models.Camera;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.LocationCamera;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.repository.CameraRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationCameraRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.service.LocationCameraService;
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
public class LocationCameraServiceImpl implements LocationCameraService {

    private final LocationCameraRepository repository;
    private final LocationCameraMapper mapper;
    private final CameraMapper cameraMapper;
    private final CameraRepository cameraRepository;
    private final LocationRepository locationRepository;
    private final Utils utils;

    @Transactional
    @Override
    public LocationCameraResponseDto assignCameraToLocation(LocationCameraRequestDto dto) {
        Camera camera = cameraRepository.findById(dto.cameraId())
                .orElseThrow(() -> new BusinessException("Camera not found", HttpStatus.NOT_FOUND));

        Location location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new BusinessException("Location not found", HttpStatus.NOT_FOUND));

        LocationCamera locationCamera = LocationCamera.builder()
                .camera(camera)
                .location(location)
                .customer(camera.getCustomer())
                .build();

        try {
            locationCamera = repository.save(locationCamera);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Camera is already assigned to this location", HttpStatus.CONFLICT);
        }

        return mapper.locationCameraToResponse(locationCamera);
    }

    @Transactional
    @Override
    public List<LocationCameraResponseDto> bulkAssignCamerasToLocation(BulkLocationCameraRequestDto dto) {
        Location location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new BusinessException("Location not found", HttpStatus.NOT_FOUND));

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

        List<LocationCamera> locationCameras = new ArrayList<>();
        for (Camera camera : cameras) {
            LocationCamera locationCamera = LocationCamera.builder()
                    .camera(camera)
                    .location(location)
                    .customer(camera.getCustomer())
                    .build();
            locationCameras.add(locationCamera);
        }

        try {
            locationCameras = repository.saveAll(locationCameras);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(
                    "One or more cameras are already assigned to this location",
                    HttpStatus.CONFLICT
            );
        }

        return locationCameras.stream()
                .map(mapper::locationCameraToResponse)
                .toList();
    }

    @Override
    public List<LocationCameraResponseDto> getCamerasByLocationId(Long locationId) {
        locationRepository.findById(locationId)
                .orElseThrow(() -> new BusinessException("Location not found", HttpStatus.NOT_FOUND));

        return repository.findByLocationIdWithCameraAndVendor(locationId)
                .stream()
                .map(mapper::locationCameraToResponse)
                .toList();
    }

    @Override
    public List<CameraAssignmentResponseDto> getAvailableCamerasForLocation(Long locationId) {
        // Validate location exists
        locationRepository.findById(locationId)
                .orElseThrow(() -> new BusinessException("Location not found", HttpStatus.NOT_FOUND));

        // Resolve customer from token
        UserData userData = utils.getLoggedInUser();
        Long customerId = userData.getCustomerId();

        // Get all cameras belonging to this customer
        List<Camera> allCameras = cameraRepository.findByCustomerIdWithDetails(customerId);

        // Get camera IDs already assigned to this location
        Set<Long> assignedCameraIds = new HashSet<>(
                repository.findCameraIdsByLocationId(locationId)
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