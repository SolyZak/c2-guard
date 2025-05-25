package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.dto.*;
import com.eden.eden_crm_sec_crm_back.mapper.*;
import com.eden.eden_crm_sec_crm_back.mapper.lookup.LKCustomerContractServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationRule;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationRuleRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerContractServiceImpl extends BaseServiceImpl<CustomerContract, Long> implements CustomerContractService {
    private final CustomerContractRepository customerContractRepository;
    private final ContractOperationRuleRepository contractOperationRuleRepository;
    private final ContractOperationRuleMapper contractOperationRuleMapper;
    private final CustomerServiceMapper customerServiceMapper;
    private final SiteDistributionCustomMapper siteDistributionMapper;
    private final SiteDistributionRepository siteDistributionRepository;
    private final CustomerContractDetailsMapper customerContractMapper;
    private final LKCustomerContractServiceMapper lkCustomerContractServiceMapper;


    @Override
    protected BaseRepository<CustomerContract, Long> getRepository() {
        return customerContractRepository;
    }

    @Override
    public CustomerContract insert(CustomerContract entity) {
        if (entity.getStatus() == null) {
            entity.setStatus("NEW");
        }
        if (entity.getCustomerContractServices() != null) {
            for (LKCustomerContractService service : entity.getCustomerContractServices()) {
                service.setCustomerContract(entity);
            }
        }
        return customerContractRepository.save(entity);
    }

    @Override
    public List<CustomerContractCustomDto> getAllCustomerAgreements() {
        List<CustomerContract> agreementsList = customerContractRepository.findAll();
        return agreementsList.stream()
                .map(agreement -> new CustomerContractCustomDto(
                        agreement.getAgreementNumber(),
                        agreement.getAgreementName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerContractDetailsDto getCustomerContractDetails(Long contractId) {

        CustomerContract contract = customerContractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("CustomerContract not found"));

         CustomerContractDetailsDto dto = customerContractMapper.map(contract);

        ContractOperationRule rule = contractOperationRuleRepository.findByCustomerAgreementId(contractId);
        if (rule != null) {
            dto.setContractOperationRuleDTO(contractOperationRuleMapper.map(rule));
        }
        List<CustomerServiceDTO> serviceDTOs = contract.getCustomerContractServices().stream()
                .map(link -> {
                    CustomerService service = link.getCustomerService();
                    CustomerServiceDTO serviceDto = customerServiceMapper.map(service);
                    return serviceDto;
                })
                .collect(Collectors.toList());
        dto.setServiceDTOList(serviceDTOs);

        // Map site distributions
        List<SiteDistribution> distributions = siteDistributionRepository.findByCustomerContractSite_Id(contractId);
        List<SiteDistributionCustomDto> siteDtos = distributions.stream()
                .map(siteDistributionMapper::map)
                .collect(Collectors.toList());

        dto.setSiteDistributionCustomDto(siteDtos);

        return dto;
    }


}
