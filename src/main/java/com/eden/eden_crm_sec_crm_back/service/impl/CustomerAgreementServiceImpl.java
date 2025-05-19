package com.eden.eden_crm_sec_crm_back.service.impl;
import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerAgreementMapper;
import com.eden.eden_crm_sec_crm_back.models.CustomerAgreement;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerAgreementService;
import com.eden.eden_crm_sec_crm_back.repository.CustomerAgreementRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerAgreementServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerAgreementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerAgreementServiceImpl extends BaseServiceImpl<CustomerAgreement, Long> implements CustomerAgreementService {
    private final CustomerAgreementRepository customerAgreementRepository;
    private final LKCustomerAgreementServiceRepository agreementServiceRepository;
    private final CustomerAgreementMapper customerAgreementMapper;

     @Override
    protected BaseRepository<CustomerAgreement, Long> getRepository() {
        return customerAgreementRepository;
    }

    @Override
    public CustomerAgreement insert(CustomerAgreement entity) {
         if(entity.getStatus() == null){
             entity.setStatus("NEW");
         }
        if (entity.getAgreementServices() != null) {
            for (LKCustomerAgreementService service : entity.getAgreementServices()) {
                service.setAgreement(entity);
            }
        }
        return customerAgreementRepository.save(entity);
    }
}
