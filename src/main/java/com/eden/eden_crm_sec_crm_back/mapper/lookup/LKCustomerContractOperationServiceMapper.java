package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface LKCustomerContractOperationServiceMapper extends BaseMapper<LKCustomerContractOperationService, LKCustomerContractOperationServiceDto> {
    @Override
    @Mapping(source = "siteDistribution.id", target = "siteDistributionId")
    LKCustomerContractOperationServiceDto map(LKCustomerContractOperationService entity);

    @Override
    @Mapping(source = "siteDistributionId", target = "siteDistribution", qualifiedByName = "mapToSiteDistribution")
    LKCustomerContractOperationService unMap(LKCustomerContractOperationServiceDto dto);

    @Named("mapToSiteDistribution")
    default SiteDistribution mapToSiteDistribution(Long id) {
        if (id == null) return null;
        SiteDistribution site = new SiteDistribution();
        site.setId(id);
        return site;
    }
}
