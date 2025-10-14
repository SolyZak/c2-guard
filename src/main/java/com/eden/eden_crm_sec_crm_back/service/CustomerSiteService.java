package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSitePremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteResponseDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;

public interface CustomerSiteService {
    CustomerSiteResponseDto addCustomerSite(CustomerSiteRequestDto requestDto);
    void updateCustomerSite(Long id, UpdateCustomerSiteRequestDto requestDto);
    List<CustomerSiteResponseDto> getSitesForCustomer();
    List<CustomerSiteResponseDto> getSitesForCustomer(Long customerId);
    String deleteSiteForCustomer(Long id);
    CustomerSite findOne(Long id, Long customerId);

    PaginateResponse<CustomerSitePremiseResponseDto>  getSitesPaginated(String search, int page, int size);
}
