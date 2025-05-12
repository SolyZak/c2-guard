package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteResponseDto;

import java.util.List;

public interface CustomerSiteService {
    CustomerSiteResponseDto addCustomerSite(CustomerSiteRequestDto requestDto);
    void updateCustomerSite(Long id, UpdateCustomerSiteRequestDto requestDto);
    void enableSite(Long siteId);
    List<CustomerSiteResponseDto> getSiteByCustomer(Long customerId, boolean activeOnly);
    List<CustomerSiteResponseDto> getNotActiveSites(Long customerId);
    List<CustomerSiteResponseDto> getSites();
    List<CustomerSiteResponseDto> getSitesByIds(List<Long> ids);
}
