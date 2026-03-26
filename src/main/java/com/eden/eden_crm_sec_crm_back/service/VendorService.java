package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.VendorRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.VendorResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;

public interface VendorService {
    VendorResponseDto create(VendorRequestDto dto);

    List<VendorResponseDto> getAll();

    PaginateResponse<VendorResponseDto> getPaginated(Integer page, Integer size);
}