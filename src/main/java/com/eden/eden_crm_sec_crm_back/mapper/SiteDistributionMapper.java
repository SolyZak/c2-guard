package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractOperationServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LKCustomerContractOperationServiceMapper.class})
public interface SiteDistributionMapper extends BaseMapper<SiteDistribution, SiteDistributionDto> {

    @Override
    @Mapping(source = "site.id", target = "siteId")
    @Mapping(source = "activities", target = "activities")
    @Mapping(source = "lkCustomerContractService.id", target = "lkCustomerContractServiceId")
    @Mapping(source = "operationServices", target = "operationServices")
    SiteDistributionDto map(SiteDistribution entity);

    @Override
    @Mapping(target = "site", source = "siteId", qualifiedByName = "mapSiteId")
    @Mapping(target = "activities", source = "activities")
    @Mapping(target = "lkCustomerContractService", source = "lkCustomerContractServiceId", qualifiedByName = "mapIdToLkService")
    @Mapping(target = "operationServices", source = "operationServices")
    SiteDistribution unMap(SiteDistributionDto dto);

    @AfterMapping
    default void setBackReferences(@MappingTarget SiteDistribution entity) {
        if (entity.getOperationServices() != null) {
            for (LKCustomerContractOperationService service : entity.getOperationServices()) {
                service.setSiteDistribution(entity);
            }
        }
    }

    @Named("mapSiteId")
    default CustomerSite mapSiteId(Long siteId) {
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