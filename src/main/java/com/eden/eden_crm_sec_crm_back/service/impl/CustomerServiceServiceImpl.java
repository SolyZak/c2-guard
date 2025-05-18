package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.repository.CustomerServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceServiceImpl extends BaseServiceImpl<CustomerService, Long> implements CustomerServiceService {

    private final CustomerServiceRepository customerServiceRepository;
    @Override
    protected BaseRepository<CustomerService, Long> getRepository() {
        return customerServiceRepository;
    }



    @Override
    public CustomerService insert(CustomerService entity) {
        if (entity.getServiceDetails() != null && !entity.getServiceDetails().isEmpty()) {
            entity.getServiceDetails().forEach(detail -> detail.setCustomerService(entity));
        }
        return super.insert(entity);
    }
}
