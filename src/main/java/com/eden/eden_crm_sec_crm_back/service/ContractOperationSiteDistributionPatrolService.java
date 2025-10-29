package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.ContractDistributionForPatrol;

import java.util.List;

public interface ContractOperationSiteDistributionPatrolService {

    void add(List<ContractDistributionForPatrol> request, Long contractId, Long serviceId);
}
