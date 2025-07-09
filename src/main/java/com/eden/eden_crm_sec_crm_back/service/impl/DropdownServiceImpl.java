package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerContractMapper;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerMapper;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerSiteMapper;
import com.eden.eden_crm_sec_crm_back.models.projections.GeneralDropdownProjection;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.DropdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DropdownServiceImpl implements DropdownService {

    private final CustomerContractRepository customerContractRepository;
    private final CustomerRepository customerRepository;
    private final CustomerSiteRepository customerSiteRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final CustomerContractMapper customerContractMapper;
    private final CustomerMapper customerMapper;
    private final CustomerSiteMapper customerSiteMapper;

    @Override
    public List<GeneralDropdown> customerContractedSecurityCompaniesDropdown(Long customerId) {
        return customerContractRepository.securityCompanies(customerId).stream().map(customerContractMapper::toDropdown).toList();
    }

    @Override
    public List<GeneralDropdown> securityCompanyCustomersDropdown(Long securityCompanyId) {
        return customerRepository.findCustomersBySecurityCompanyId(securityCompanyId)
                .stream()
                .map(customerMapper::toDropdown)
                .toList();
    }

    @Override
    public List<GeneralDropdown> securityCompanyContractsDropdown(
            Long securityCompanyId,
            List<Long> customerId
    ) {
        return customerContractRepository.contractsDropdown(
                        securityCompanyId,
                        customerId != null && !customerId.isEmpty() ? customerId : null
                )
                .stream()
                .map(customerContractMapper::toDropdown)
                .toList();
    }

    @Override
    public List<GeneralDropdown> myContractsDropdown(Long customerId, Long securityCompanyId) {
        // todo must include status once we impl status controlling
        return customerContractRepository.contractsDropdown(customerId, securityCompanyId).stream().map(customerContractMapper::toDropdown).toList();
    }

    @Override
    public List<GeneralDropdown> myOperationSitesDropdown(Long customerId, Long securityCompanyId, List<Long> contractId) {
        List<GeneralDropdownProjection> operationSites = securityCompanyId == null && contractId == null ?
                customerSiteRepository.operationSitesDropdown(customerId) :
                siteDistributionRepository.operationSitesDropdown(customerId, securityCompanyId, contractId);
        return operationSites.stream().map(customerContractMapper::toDropdown).toList();
    }

    @Override
    public List<GeneralDropdown> securityCompanyOperationSitesDropdown(Long securityCompanyId, List<Long> customerId, List<Long> contractId) {
        return siteDistributionRepository.operationSitesDropdown(
                        securityCompanyId,
                        customerId != null && !customerId.isEmpty() ? customerId : null,
                        contractId != null && !contractId.isEmpty() ? contractId : null
                )
                .stream()
                .map(customerSiteMapper::toDropdown)
                .toList();
    }
}
