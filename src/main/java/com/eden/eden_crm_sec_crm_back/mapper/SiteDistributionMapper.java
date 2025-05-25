package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractOperationServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LKCustomerContractOperationServiceMapper.class})
public interface SiteDistributionMapper extends BaseMapper<SiteDistribution, SiteDistributionDto> {


        @Override
        @Mapping(source = "lkCustomerContractService.id", target = "lkCustomerContractServiceId")
        SiteDistributionDto map(SiteDistribution entity);

        @Override
        @Mapping(target = "lkCustomerContractService", source = "lkCustomerContractServiceId", qualifiedByName = "mapIdToLkService")
        @Mapping(target = "operationServices", ignore = true) // Optional: if not mapping services now
        SiteDistribution unMap(SiteDistributionDto dto);

        @Named("mapIdToLkService")
        default LKCustomerContractService mapIdToLkService(Long serviceId) {
            if (serviceId == null) return null;
            LKCustomerContractService service = new LKCustomerContractService();
            service.setId(serviceId);
            return service;
        }

}