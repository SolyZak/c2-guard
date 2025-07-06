package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractDetailsData;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractRowDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractServiceDetailsData;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractWithRules;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.projections.GeneralDropdownProjection;
import org.mapstruct.*;

import java.time.LocalTime;


@Mapper(componentModel = "spring")
public interface CustomerContractMapper {
    CustomerContract toEntity(AddContractDto dto);

    LKCustomerContractService toEntity(AddContractServiceDto d);

    ContractRowDto toContractRowDto(CustomerContract e);

    @Mapping(target = "serviceName", source = "cs.customerService.customerService.serviceName")
    @Mapping(target = "serviceActivities", source = "cs.customerService.customerService.activities")
    @Mapping(target = "serviceMultiSite", source = "cs.customerService.customerService.multiSite")
    @Mapping(target = "hours", source = "cs.customerService.hours")
    @Mapping(target = "days", source = "cs.customerService.days")
    @Mapping(target = "distributedQuantity", source = "cs.distributedQuantity")
    ContractServiceDetailsData toContractServiceDetailsData(LKCustomerContractService cs);

    @Mapping(target = "checkInBeforeMinutes", source = "c.customerAgreement.checkInBeforeMinutes")
    @Mapping(target = "checkInAfterMinutes", source = "c.customerAgreement.checkInAfterMinutes")
    @Mapping(target = "checkOutBeforeMinutes", source = "c.customerAgreement.checkOutBeforeMinutes")
    @Mapping(target = "presenceMode", source = "c.customerAgreement.presenceMode")
    ContractWithRules toContractWithRules(CustomerContract c);

    @Mapping(target = "id", source = "c.id")
    @Mapping(target = "name", source = "c.agreementName")
    GeneralDropdown toDropdown(CustomerContract c);

    GeneralDropdown toDropdown(GeneralDropdownProjection c);

    @Mapping(target = "checkInBeforeMinutes", source = "e.customerAgreement.checkInBeforeMinutes")
    @Mapping(target = "checkInAfterMinutes", source = "e.customerAgreement.checkInAfterMinutes")
    @Mapping(target = "checkOutBeforeMinutes", source = "e.customerAgreement.checkOutBeforeMinutes")
    @Mapping(target = "presenceMode", source = "e.customerAgreement.presenceMode")
    ContractDetailsData fromEntity(CustomerContract e);

    @Mapping(target = "name", source = "e.customerService.customerService.serviceName")
    @Mapping(target = "hours", source = "e.customerService.hours")
    @Mapping(target = "days", source = "e.customerService.days")
    ContractDetailsData.ContractServiceDetails fromEntity(LKCustomerContractService e);

    @Mapping(target = "fromTime", source = "from")
    @Mapping(target = "toTime", source = "to")
    ContractDetailsData.ContractServiceDetails.ContractServiceDistributionsData.ContractOperationServiceDetails
        fromEntity(LKCustomerContractOperationService e, LocalTime from, LocalTime to);
}

