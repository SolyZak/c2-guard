package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;

import java.util.List;

public interface ContractDistributeService {
    String contractDistribute(Long contractId, Long serviceId, List<SiteDistributionDto> listDto);
}
