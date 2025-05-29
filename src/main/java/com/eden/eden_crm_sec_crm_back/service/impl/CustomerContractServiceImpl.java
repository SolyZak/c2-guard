package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractServiceDto;
import com.eden.eden_crm_sec_crm_back.mapper.*;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.ServiceDetailsRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerContractServiceImpl implements CustomerContractService {
    private final CustomerContractRepository customerContractRepository;
    private final com.eden.eden_crm_sec_crm_back.service.CustomerService customerService;
    private final CustomerContractMapper contractMapper;
    private final ServiceDetailsRepository serviceDetailsRepository;

    @Override
    @Transactional
    public String createAgreement(AddContractDto dto) {
        Customer customer = customerService.getLoggedInCustomer();
        CustomerContract contract = contractMapper.toEntity(dto);
        contract.setCustomer(customer);

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
}
