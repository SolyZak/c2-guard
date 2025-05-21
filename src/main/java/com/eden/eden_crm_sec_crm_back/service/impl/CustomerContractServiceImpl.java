package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.dto.CustomerContractCustomDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
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
}
