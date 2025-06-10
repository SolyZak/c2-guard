package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.external.CustomerInfo;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteData;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteInfo;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;

import java.util.List;

public interface ExternalService {
    OperationSiteInfo getOperationSiteDetails(Long id);
    List<OperationSiteData> getCustomerOperationSites(Long customerId);
    CustomerInfo getCustomerInfo(Long id);
    WorkforceSiteDistributionDto operationSiteServicesDropdown(Long id);
}
