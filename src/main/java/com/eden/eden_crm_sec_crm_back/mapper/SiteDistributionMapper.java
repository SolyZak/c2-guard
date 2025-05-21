package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SiteDistributionMapper extends BaseMapper<SiteDistribution, SiteDistributionDto> {
    @Override
    @Mapping(source = "site.id", target = "siteId")
    @Mapping(source = "customerContract.id", target = "customerContractId") // Fixed field name
    SiteDistributionDto map(SiteDistribution entity);

    @Override
    @Mapping(target = "site", source = "siteId", qualifiedByName = "mapIdToSite")
    @Mapping(target = "customerContract", source = "customerContractId", qualifiedByName = "mapIdToContract")
    SiteDistribution unMap(SiteDistributionDto dto);

    @Named("mapIdToSite")
    default CustomerSite mapIdToSite(Long siteId) {
        if (siteId == null) {
            return null;
        }
        CustomerSite site = new CustomerSite();
        site.setId(siteId);
        return site;
    }

    @Named("mapIdToContract")
    default CustomerContract mapIdToContract(Long contractId) {
        if (contractId == null) {
            throw new IllegalArgumentException("CustomerContract ID cannot be null");
        }
        CustomerContract contract = new CustomerContract();
        contract.setId(contractId);
        return contract;
    }
}
