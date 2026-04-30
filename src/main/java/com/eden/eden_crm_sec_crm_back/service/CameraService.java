package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.CreateCameraRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;

public interface CameraService {
    CameraResponseDto create(CreateCameraRequest dto);

    List<CameraResponseDto> getAll();

    PaginateResponse<CameraResponseDto> getPaginated(Integer page, Integer size);
}

