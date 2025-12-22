package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.ValidateLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.ValidateQrRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.List;

public interface LocationService {
    void addNewLocation(AddLocationRequest request) throws IOException, WriterException;
    PaginateResponse<PremiseLocationDto> getLocationsPaginated(String search, int page, int size);

    List<LocationWithPremiseDto> findLoggedInCustomerLocations();

    List<LocationResponseDto> findLoggedInCustomerLocationsByPatrolId(Long patrolId);

    ValidateQrResponse validateQr(ValidateQrRequest request);

    ValidateLocationResponse validateLocation(Long locationId, ValidateLocationRequest locationRequest);
}
