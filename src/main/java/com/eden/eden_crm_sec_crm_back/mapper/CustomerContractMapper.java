package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractServiceDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.mapstruct.*;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = LKCustomerContractServiceMapper.class)
public interface CustomerContractMapper extends BaseMapper<CustomerContract, CustomerContractDto> {

    @Override
    @Mapping(target = "lkCustomerContractServiceDto", source = "customerContractServices", qualifiedByName = "firstService")
    CustomerContractDto map(CustomerContract entity);

    @Override
    @Mapping(target = "customerContractServices", source = "lkCustomerContractServiceDto", qualifiedByName = "dtoToList")
    CustomerContract unMap(CustomerContractDto dto);

    @Named("firstService")
    default LKCustomerContractServiceDto mapFirstService(List<LKCustomerContractService> services) {
        if (services == null || services.isEmpty()) return null;
        return new LKCustomerContractServiceDto(
                services.get(0).getId(),
                services.get(0).getQuantity(),
                services.get(0).getUnitPrice(),
                services.get(0).getCustomerService().getId(),
                services.get(0).getCustomerContract().getId()
        );
    }

    @Named("dtoToList")
    default List<LKCustomerContractService> mapDtoToList(LKCustomerContractServiceDto dto) {
        if (dto == null) return Collections.emptyList();
        LKCustomerContractService service = new LKCustomerContractService();
        service.setId(dto.getId());
        service.setQuantity(dto.getQuantity());
        service.setUnitPrice(dto.getUnitPrice());

        if (dto.getCustomerServiceId() != null) {
            CustomerService customerService = new CustomerService();
            customerService.setId(dto.getCustomerServiceId());
            service.setCustomerService(customerService);
        }

        if (dto.getCustomerContractId() != null) {
            CustomerContract contract = new CustomerContract();
            contract.setId(dto.getCustomerContractId());
            service.setCustomerContract(contract);
        }

        return Collections.singletonList(service);
    }
}

//@Mapper(componentModel = "spring")
//public interface CustomerContractMapper extends BaseMapper<CustomerContract, CustomerContractDto> {
//    @Override
//    @Mapping(target = "agreementServicesIds", source = "customerContractServices", qualifiedByName = "servicesToIds")
//    CustomerContractDto map(CustomerContract entity);
//
//    @Override
//    @Mapping(target = "customerContractServices", source = "agreementServicesIds", qualifiedByName = "idsToServices")
//    CustomerContract unMap(CustomerContractDto dto);
//
//    @Named("servicesToIds")
//    default List<Long> mapServicesToIds(List<LKCustomerContractService> services) {
//        if (services == null || services.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return services.stream()
//                .map(LKCustomerContractService::getId)
//                .collect(Collectors.toList());
//    }
//
//    @Named("idsToServices")
//    default List<LKCustomerContractService> mapIdsToServices(List<Long> ids) {
//        if (ids == null || ids.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return ids.stream()
//                .map(id -> {
//                    LKCustomerContractService service = new LKCustomerContractService();
//                    service.setId(id);
//                    return service;
//                })
//                .collect(Collectors.toList());
//    }
//}
