package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDataDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDetailsDropdownDto;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;

public interface CustomerServiceService {
     PaginateResponse<ServiceDataDto> paginateMyServices(Integer page, Integer size);
     List<ServiceDetailsDropdownDto> allMyServiceDetails();
     String create(AddServiceDto dto);
}
