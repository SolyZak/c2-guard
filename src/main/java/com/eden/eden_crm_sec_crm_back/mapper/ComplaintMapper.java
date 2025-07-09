package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.response.Complaint;
import com.eden.eden_crm_sec_crm_back.models.ComplaintEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {
    @Mapping(target = "contractName", source = "entity.contract.agreementName")
    @Mapping(target = "contractId", source = "entity.contract.id")
    @Mapping(target = "customerName", source = "entity.customer.name")
    @Mapping(target = "customerId", source = "entity.customer.id")
    @Mapping(target = "operationSiteName", source = "entity.customerSite.name")
    @Mapping(target = "operationSiteId", source = "entity.customerSite.id")
    @Mapping(target = "creationDate", source = "entity.createdDate")
    @Mapping(target = "images", source = "images")
    Complaint toComplaint(ComplaintEntity entity, List<String> images);
}
