package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionCustomDto;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SiteDistributionCustomMapper extends BaseMapper<SiteDistribution, SiteDistributionCustomDto> {
    @Override
    SiteDistributionCustomDto map(SiteDistribution entity);

    @Override
    SiteDistribution unMap(SiteDistributionCustomDto dto);
}
