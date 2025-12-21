package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.LocationResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.LocationWithPremiseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseLocationDto;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.List;

public interface LocationService {
    void addNewLocation(AddLocationRequest request) throws IOException, WriterException;
    PaginateResponse<PremiseLocationDto> getLocationsPaginated(String search, int page, int size);

    List<LocationWithPremiseDto> findLoggedInCustomerLocations();

    List<LocationResponseDto> findLoggedInCustomerLocationsByPatrolId(Long patrolId);
}
