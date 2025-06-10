package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.external.CustomerInfo;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteData;
import com.eden.eden_crm_sec_crm_back.dto.external.OperationSiteInfo;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.ExternalMapper;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.ExternalService;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerRepository customerRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final WorkforceService workforceService;
    private final ExternalMapper externalMapper;

    // This function will provide information abut operation site today status
    @Override
    public OperationSiteInfo getOperationSiteDetails(Long id) {
        CustomerSite operationSite = customerSiteRepository.findById(id).orElseThrow(
                () -> new BusinessException("Can`t find operation site by id: %d".formatted(id), HttpStatus.NOT_FOUND)
        );
        Optional<SiteDistribution> firstSiteDistributed = siteDistributionRepository.findByActiveTodayAndSiteId(id, LocalDate.now()).stream().findFirst();

         OperationSiteInfo.OperationSiteInfoBuilder operationSiteInfoBuilder = OperationSiteInfo.builder()
                .id(operationSite.getId())
                .name(operationSite.getName())
                .operationSiteName(operationSite.getName())
                .operationSiteId(operationSite.getId())
                .customerId(operationSite.getCustomer().getId())
                .customerName(operationSite.getCustomer().getName());

         if (firstSiteDistributed.isPresent()) {
             CustomerContract contract = firstSiteDistributed.get().getCustomerContract();
             operationSiteInfoBuilder
                     .contractName(contract.getAgreementName())
                     .contractId(contract.getId())
                     .securityCompanyId(contract.getSecurityCompanyId())
                     .securityCompanyName(contract.getSecurityCompanyName());
         }

         return operationSiteInfoBuilder.build();
    }

    @Override
    public List<OperationSiteData> getCustomerOperationSites(Long customerId) {
        return customerSiteRepository.findByCustomerId(customerId).stream().map(externalMapper::fromEntity).toList();
    }

    @Override
    public CustomerInfo getCustomerInfo(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new BusinessException("Can`t find customer by id: %d".formatted(id), HttpStatus.NOT_FOUND)
        );
        return CustomerInfo.builder()
                .id(customer.getId())
                .name(customer.getName())
                .code(customer.getCode())
                .build();
    }

    @Override
    public WorkforceSiteDistributionDto operationSiteServicesDropdown(Long id) {
        return workforceService.operationSiteServicesDropdown(id);
    }
}
