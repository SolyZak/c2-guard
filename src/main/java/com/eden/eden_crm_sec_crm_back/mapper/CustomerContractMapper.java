package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.mapstruct.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface CustomerContractMapper extends BaseMapper<CustomerContract, CustomerContractDto> {
    @Override
    @Mapping(target = "agreementServicesIds", source = "customerContractServices", qualifiedByName = "servicesToIds")
    CustomerContractDto map(CustomerContract entity);

    @Override
    @Mapping(target = "customerContractServices", source = "agreementServicesIds", qualifiedByName = "idsToServices")
    CustomerContract unMap(CustomerContractDto dto);

    @Named("servicesToIds")
    default List<Long> mapServicesToIds(List<LKCustomerContractService> services) {
        if (services == null || services.isEmpty()) {
            return Collections.emptyList();
        }
        return services.stream()
                .map(LKCustomerContractService::getId)
                .collect(Collectors.toList());
    }

    @Named("idsToServices")
    default List<LKCustomerContractService> mapIdsToServices(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream()
                .map(id -> {
                    LKCustomerContractService service = new LKCustomerContractService();
                    service.setId(id);
                    return service;
                })
                .collect(Collectors.toList());
    }
}
