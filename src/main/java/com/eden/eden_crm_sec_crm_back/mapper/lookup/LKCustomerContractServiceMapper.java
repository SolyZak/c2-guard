package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractServiceDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface LKCustomerContractServiceMapper extends BaseMapper<LKCustomerContractService, LKCustomerContractServiceDto> {

    @Override
    @Mapping(source = "customerService.id", target = "serviceDetailsId") // Now refers to ServiceDetails
    @Mapping(source = "customerContract.id", target = "customerContractId")
    LKCustomerContractServiceDto map(LKCustomerContractService entity);

    @Override
    @Mapping(source = "serviceDetailsId", target = "customerService", qualifiedByName = "idToServiceDetails")
    @Mapping(source = "customerContractId", target = "customerContract", qualifiedByName = "idToContract")
    LKCustomerContractService unMap(LKCustomerContractServiceDto dto);

    @Named("idToServiceDetails")
    default ServiceDetails idToServiceDetails(Long id) {
        if (id == null) return null;
        ServiceDetails details = new ServiceDetails();
        details.setId(id);
        return details;
    }

    @Named("idToContract")
    default CustomerContract idToContract(Long id) {
        if (id == null) return null;
        CustomerContract contract = new CustomerContract();
        contract.setId(id);
        return contract;
    }
}
