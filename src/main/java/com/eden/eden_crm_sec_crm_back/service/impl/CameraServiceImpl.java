package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.CreateCameraRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.CameraMapper;
import com.eden.eden_crm_sec_crm_back.models.Camera;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Vendor;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CameraRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.VendorRepository;
import com.eden.eden_crm_sec_crm_back.service.CameraService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {
    private final CameraRepository repository;
    private final CameraMapper mapper;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;

    @Transactional
    @Override
    public CameraResponseDto create(CreateCameraRequest dto) {
        // Validate vendor exists
        Vendor vendor = vendorRepository.findById(dto.vendorId())
                .orElseThrow(() -> new BusinessException("Vendor not found", HttpStatus.NOT_FOUND));

        // Validate customer exists
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));

        Camera camera = mapper.requestToCamera(dto);
        camera.setVendor(vendor);
        camera.setCustomer(customer);
        camera = repository.save(camera);

        return mapper.cameraToResponse(camera);
    }

    @Override
    public List<CameraResponseDto> getAll() {
        return repository.findAllWithVendorAndCustomer()
                .stream()
                .map(mapper::cameraToResponse)
                .toList();
    }

    @Override
    public PaginateResponse<CameraResponseDto> getPaginated(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Camera> cameras = repository.findAllPaginatedWithVendorAndCustomer(pageable);

        return new PaginateResponse<>(
                cameras.getContent().stream().map(mapper::cameraToResponse).toList(),
                page,
                size,
                cameras.getTotalElements(),
                (long) cameras.getTotalPages()
        );
    }
}