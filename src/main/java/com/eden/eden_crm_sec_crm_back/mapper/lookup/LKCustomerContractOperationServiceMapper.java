package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.mapper.WorkSiteDistributionLocationMapper;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.WorkSiteDistributionLocation;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = WorkSiteDistributionLocationMapper.class)
public interface LKCustomerContractOperationServiceMapper extends BaseMapper<LKCustomerContractOperationService, LKCustomerContractOperationServiceDto> {

    @Override
    @Mapping(source = "workSiteDistributionLocation", target = "workSiteDistributionLocation")
    LKCustomerContractOperationServiceDto map(LKCustomerContractOperationService entity);

    @Override
    @Mapping(source = "workSiteDistributionLocation", target = "workSiteDistributionLocation")
    LKCustomerContractOperationService unMap(LKCustomerContractOperationServiceDto dto);
}
