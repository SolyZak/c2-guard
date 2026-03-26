package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.VendorRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.VendorResponseDto;
import com.eden.eden_crm_sec_crm_back.models.Vendor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VendorMapper {
    Vendor requestToVendor(VendorRequestDto dto);

    VendorResponseDto vendorToResponse(Vendor vendor);
}

