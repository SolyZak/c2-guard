package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.LocationCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.LocationCameraResponseDto;
import com.eden.eden_crm_sec_crm_back.models.LocationCamera;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CameraMapper.class})
public interface LocationCameraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "camera", ignore = true)
    @Mapping(target = "customer", ignore = true)
    LocationCamera requestToLocationCamera(LocationCameraRequestDto request);

    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "customerId", source = "customer.id")
    LocationCameraResponseDto locationCameraToResponse(LocationCamera locationCamera);
}