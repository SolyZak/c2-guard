package com.eden.eden_crm_sec_crm_back.mapper.lookup;

import com.eden.eden_crm_sec_crm_back.base.mapper.BaseMapper;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface LKCustomerContractOperationServiceMapper extends BaseMapper<LKCustomerContractOperationService, LKCustomerContractOperationServiceDto> {
}
