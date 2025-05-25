package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.WorkSiteDistributionLocation;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface LKCustomerContractOperationServiceMapper extends BaseMapper<LKCustomerContractOperationService, LKCustomerContractOperationServiceDto> {
    @Override
    @Mapping(source = "workSiteDistributionLocation.id", target = "workSiteDistributionLocationId")
    LKCustomerContractOperationServiceDto map(LKCustomerContractOperationService entity);

    @Override
    @Mapping(target = "workSiteDistributionLocation", source = "workSiteDistributionLocationId", qualifiedByName = "mapLocationId")
    LKCustomerContractOperationService unMap(LKCustomerContractOperationServiceDto dto);

    @Named("mapLocationId")
    default WorkSiteDistributionLocation mapLocationId(Long id) {
        if (id == null) return null;
        WorkSiteDistributionLocation location = new WorkSiteDistributionLocation();
        location.setId(id);
        return location;
    }
//    @Override
//    LKCustomerContractOperationServiceDto map(LKCustomerContractOperationService entity);
//
//    @Override
//    @Mapping(target = "workSiteDistributionLocation", ignore = true) // Handle manually
//    LKCustomerContractOperationService unMap(LKCustomerContractOperationServiceDto dto);
}
