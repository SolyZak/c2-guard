package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.CustomerAgreementDTO;
import com.eden.eden_crm_sec_crm_back.models.CustomerAgreement;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerAgreementService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface CustomerAgreementMapper extends BaseMapper<CustomerAgreement, CustomerAgreementDTO> {
    @Override
    @Mapping(target = "agreementServicesIds", source = "agreementServices", qualifiedByName = "servicesToIds")
    CustomerAgreementDTO map(CustomerAgreement entity);

    @Override
    @Mapping(target = "agreementServices", source = "agreementServicesIds", qualifiedByName = "idsToServices")
    CustomerAgreement unMap(CustomerAgreementDTO dto);

    @Named("servicesToIds")
    default List<Long> mapServicesToIds(List<LKCustomerAgreementService> services) {
        if (services == null || services.isEmpty()) {
            return Collections.emptyList();
        }
        return services.stream()
                .map(LKCustomerAgreementService::getId)
                .collect(Collectors.toList());
    }

    @Named("idsToServices")
    default List<LKCustomerAgreementService> mapIdsToServices(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream()
                .map(id -> {
                    LKCustomerAgreementService service = new LKCustomerAgreementService();
                    service.setId(id);
                    return service;
                })
                .collect(Collectors.toList());
    }
//    @Override
//    @Mapping(target = "agreementServicesIds", source = "agreementServices", qualifiedByName = "servicesToIds")
//    CustomerAgreementDTO map(CustomerAgreement entity);
//
//    @Override
//    @Mapping(target = "agreementServices", ignore = true) // Will be handled separately
//    CustomerAgreement unMap(CustomerAgreementDTO dto);
//
//    @Named("servicesToIds")
//    default List<Long> mapServicesToIds(List<LKCustomerAgreementService> services) {
//        if (services == null || services.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return services.stream()
//                .map(LKCustomerAgreementService::getId)
//                .collect(Collectors.toList());
//    }
}
