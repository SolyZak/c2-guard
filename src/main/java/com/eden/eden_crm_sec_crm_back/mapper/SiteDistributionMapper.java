package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SiteDistributionMapper extends BaseMapper<SiteDistribution, SiteDistributionDto> {

    @Override
    @Mapping(source = "site.id", target = "siteId")
    @Mapping(source = "lkCustomerContractService.id", target = "lkCustomerContractServiceId")
    SiteDistributionDto map(SiteDistribution entity);

    @Override
    @Mapping(target = "site", source = "siteId", qualifiedByName = "mapIdToSite")
    @Mapping(target = "lkCustomerContractService", source = "lkCustomerContractServiceId", qualifiedByName = "mapIdToLkService")
    SiteDistribution unMap(SiteDistributionDto dto);

    @Named("mapIdToSite")
    default CustomerSite mapIdToSite(Long siteId) {
        if (siteId == null) return null;
        CustomerSite site = new CustomerSite();
        site.setId(siteId);
        return site;
    }

    @Named("mapIdToLkService")
    default LKCustomerContractService mapIdToLkService(Long serviceId) {
        if (serviceId == null) return null;
        LKCustomerContractService service = new LKCustomerContractService();
        service.setId(serviceId);
        return service;
    }
}