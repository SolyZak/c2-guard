package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractRowDto;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = LKCustomerContractServiceMapper.class)
public interface CustomerContractMapper {
    CustomerContract toEntity(AddContractDto dto);

    LKCustomerContractService toEntity(AddContractServiceDto d);

    ContractRowDto toContractRowDto(CustomerContract e);
}

