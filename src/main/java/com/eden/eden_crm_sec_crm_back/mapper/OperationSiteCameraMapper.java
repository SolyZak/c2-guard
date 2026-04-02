package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.OperationSiteCameraRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.OperationSiteCameraResponseDto;
import com.eden.eden_crm_sec_crm_back.models.OperationSiteCamera;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CameraMapper.class})
public interface OperationSiteCameraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "camera", ignore = true)
    @Mapping(target = "operationSite", ignore = true)
    OperationSiteCamera requestToOperationSiteCamera(OperationSiteCameraRequestDto request);

    @Mapping(target = "operationSiteId", source = "operationSite.id")
        // camera → CameraResponseDto is handled automatically by CameraMapper (declared in 'uses')
    OperationSiteCameraResponseDto operationSiteCameraToResponse(OperationSiteCamera operationSiteCamera);
}