package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.WorkSiteDistributionLocationDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.WorkSiteDistributionLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface WorkSiteDistributionLocationMapper extends BaseMapper<WorkSiteDistributionLocation, WorkSiteDistributionLocationDto> {
    @Override
    @Mapping(source = "site.id", target = "siteId")
    @Mapping(source = "siteDistribution.id", target = "siteDistributionId")
    WorkSiteDistributionLocationDto map(WorkSiteDistributionLocation entity);

    @Override
    @Mapping(target = "site", source = "siteId", qualifiedByName = "mapSiteId")
    @Mapping(target = "siteDistribution", source = "siteDistributionId", qualifiedByName = "mapSiteDistributionId")
    WorkSiteDistributionLocation unMap(WorkSiteDistributionLocationDto dto);

    @Named("mapSiteId")
    default CustomerSite mapSiteId(Long siteId) {
        if (siteId == null) return null;
        CustomerSite site = new CustomerSite();
        site.setId(siteId);
        return site;
    }

    @Named("mapSiteDistributionId")
    default SiteDistribution mapSiteDistributionId(Long id) {
        if (id == null) return null;
        SiteDistribution sd = new SiteDistribution();
        sd.setId(id);
        return sd;
    }
}
