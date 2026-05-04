package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerPaginateDto;
import com.eden.eden_crm_sec_crm_back.dto.request.CustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.MessageResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    CustomerResponseDto create(CustomerRequestDto dto, MultipartFile logo);
    PaginateResponse<CustomerResponseDto> paginate(CustomerPaginateDto dto);
    List<CustomerResponseDto> all(String keyword);
    MessageResponse delete(Long id);
    CustomerResponseDto show(Long id);
    CustomerResponseDto update(Long id, UpdateCustomerRequestDto dto, MultipartFile logo);
    void enableCustomer(Long customerId);
}
