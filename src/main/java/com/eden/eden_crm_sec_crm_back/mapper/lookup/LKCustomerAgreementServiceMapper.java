package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerAgreementServiceDto;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerAgreementService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface LKCustomerAgreementServiceMapper extends BaseMapper<LKCustomerAgreementService, LKCustomerAgreementServiceDto> {

    @Override
    @Mapping(source = "customerService", target = "customerService")
    @Mapping(source = "agreement.id", target = "agreementId")
    LKCustomerAgreementServiceDto map(LKCustomerAgreementService entity);

    @Override
    @Mapping(source = "customerService", target = "customerService")
    @Mapping(target = "agreement.id", source = "agreementId")
    LKCustomerAgreementService unMap(LKCustomerAgreementServiceDto dto);



}
