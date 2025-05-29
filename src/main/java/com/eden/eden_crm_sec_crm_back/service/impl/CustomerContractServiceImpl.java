package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.Currency;
import com.eden.eden_crm_sec_crm_back.clients.dto.SecurityCompanyData;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractRowDto;
import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.*;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.ServiceDetailsRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerContractServiceImpl implements CustomerContractService {
    private final CustomerContractRepository customerContractRepository;
    private final com.eden.eden_crm_sec_crm_back.service.CustomerService customerService;
    private final CustomerContractMapper contractMapper;
    private final ServiceDetailsRepository serviceDetailsRepository;
    private final OrgUnitClient orgUnitClient;

    @Override
    @Transactional
    public String createAgreement(AddContractDto dto) {
        Customer customer = customerService.getLoggedInCustomer();
        SecurityCompanyData securityCompanyData = null;
        try {
            securityCompanyData = orgUnitClient.getSecurityCompanyDetails(dto.getSecurityCompanyId());
        } catch (Exception e) {
            log.error("Can`t get security company info from org unit service, error: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("security-company")}), HttpStatus.NOT_FOUND);
        }
        Currency currency = null;
        try {
            currency = orgUnitClient.getCurrencyDetails(dto.getCurrency());
        } catch (Exception e) {
            log.error("Can`t get currency info from org unit service, error: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("currency")}), HttpStatus.NOT_FOUND);
        }
        CustomerContract contract = contractMapper.toEntity(dto);
        contract.setCustomer(customer);
        contract.setStatus(ContractStatus.SAVED);
        contract.setSecurityCompanyName(securityCompanyData != null ? securityCompanyData.name() : "");
        contract.setCurrencyName(currency != null ? currency.name() : "");
        contract.setCurrencyCode(currency != null ? currency.code() : "");

        List<LKCustomerContractService> contractServices = new ArrayList<>();
        Map<Long, ServiceDetails> serviceDetailsMap = serviceDetailsRepository
                .findAllById(dto.getServices().stream()
                        .map(AddContractServiceDto::getServiceDetailsId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(ServiceDetails::getId, Function.identity()));
        dto.getServices().forEach(serviceDetailDto -> {
            ServiceDetails serviceDetails = serviceDetailsMap.get(serviceDetailDto.getServiceDetailsId());
            if (serviceDetails != null) {
                LKCustomerContractService contractService = contractMapper.toEntity(serviceDetailDto);
                contractService.setCustomerService(serviceDetails);
                contractService.setCustomerContract(contract);
                contractServices.add(contractService);
            }
        });

        contract.setCustomerContractServices(contractServices);
        customerContractRepository.save(contract);
        return MessageUtil.getMessage("entity.created", new Object[]{MessageUtil.getMessage("contract")});
    }

    @Override
    public PaginateResponse<ContractRowDto> paginateMyContracts(String search, LocalDate from, LocalDate to, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Customer customer = customerService.getLoggedInCustomer();
        Page<CustomerContract> contracts = customerContractRepository.paginateByCustomer(customer.getId(), search, from, to, pageable);
        return new PaginateResponse<>(
                contracts.getContent().stream().map(contractMapper::toContractRowDto).toList(),
                page,
                size,
                contracts.getTotalElements(),
                (long) contracts.getTotalPages()
        );
    }
}
