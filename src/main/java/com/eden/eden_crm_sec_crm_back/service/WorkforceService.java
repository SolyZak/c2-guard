package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.WorkforceLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;

import java.util.List;

public interface WorkforceService {
    List<GeneralDropdown> customersDropdown();

    List<GeneralDropdown> customersContractDropdown(Long customerId);

    List<WorkforceSiteDistributionDto> customersContractOperationSitesDropdown(Long customerId, Long contractId);

    List<GeneralDropdown> operationSitesDropdown();

    WorkforceSiteDistributionDto operationSiteServicesDropdown(Long id, Long contractId);

    WorkforceFullDataDto getLoggedInWorkforce();

    void addWorkforceLocation(Long id, WorkforceLocationRequest workforceLocationRequest);
}
