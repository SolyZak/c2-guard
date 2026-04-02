package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.VendorRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.VendorResponseDto;
import com.eden.eden_crm_sec_crm_back.mapper.VendorMapper;
import com.eden.eden_crm_sec_crm_back.models.Vendor;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.VendorRepository;
import com.eden.eden_crm_sec_crm_back.service.VendorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository repository;
    private final VendorMapper mapper;

    @Transactional
    @Override
    public VendorResponseDto create(VendorRequestDto dto) {
        Vendor vendor = mapper.requestToVendor(dto);
        vendor = repository.save(vendor);
        return mapper.vendorToResponse(vendor);
    }

    @Override
    public List<VendorResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::vendorToResponse)
                .toList();
    }

    @Override
    public PaginateResponse<VendorResponseDto> getPaginated(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vendor> vendors = repository.findAll(pageable);

        return new PaginateResponse<>(
                vendors.getContent().stream().map(mapper::vendorToResponse).toList(),
                page,
                size,
                vendors.getTotalElements(),
                (long) vendors.getTotalPages()
        );
    }
}