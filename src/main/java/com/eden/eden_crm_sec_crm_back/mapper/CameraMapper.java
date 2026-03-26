package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.CreateCameraRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.CameraResponseDto;
import com.eden.eden_crm_sec_crm_back.models.Camera;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Vendor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CameraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Camera requestToCamera(CreateCameraRequest request);

    // MapStruct auto-maps matching field names in nested objects:
    //   Camera.vendor (Vendor)     → CameraResponseDto.vendor (VendorData)
    //   Camera.customer (Customer) → CameraResponseDto.customer (CustomerData)
    CameraResponseDto cameraToResponse(Camera camera);

    // Explicit sub-mapping methods so MapStruct knows how to convert the nested types
    CameraResponseDto.VendorData vendorToVendorData(Vendor vendor);

    CameraResponseDto.CustomerData customerToCustomerData(Customer customer);
}