package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteData;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExternalMapper {
    OperationSiteData fromEntity(CustomerSite e);
}
