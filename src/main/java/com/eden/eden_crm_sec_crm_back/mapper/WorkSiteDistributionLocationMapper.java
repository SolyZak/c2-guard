package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.WorkSiteDistributionLocationDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.WorkSiteDistributionLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface WorkSiteDistributionLocationMapper extends BaseMapper<WorkSiteDistributionLocation, WorkSiteDistributionLocationDto> {
    @Override
    @Mapping(source = "site.id", target = "siteId")
    WorkSiteDistributionLocationDto map(WorkSiteDistributionLocation entity);

    @Override
    @Mapping(target = "site", source = "siteId", qualifiedByName = "mapSiteId")
    WorkSiteDistributionLocation unMap(WorkSiteDistributionLocationDto dto);

    @Named("mapSiteId")
    default CustomerSite mapSiteId(Long siteId) {
        if (siteId == null) return null;
        CustomerSite site = new CustomerSite();
        site.setId(siteId);
        return site;
    }
}
