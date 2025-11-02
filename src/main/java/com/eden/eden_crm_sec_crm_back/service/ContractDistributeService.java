package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractDistributionResponseDto;
import com.eden.eden_crm_sec_crm_back.models.projections.DistributionTimesProjection;

import java.util.List;

public interface ContractDistributeService {
    ContractDistributionResponseDto contractDistribute(Long contractId, Long serviceId, List<SiteDistributionDto> listDto);
    List<DistributionTimesProjection> getAllStartEndTimesForDistribution(Long distributionId);
}
