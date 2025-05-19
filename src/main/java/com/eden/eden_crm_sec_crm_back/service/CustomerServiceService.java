package com.eden.eden_crm_sec_crm_back.service;
import com.eden.eden_crm_sec_crm_back.base.service.BaseService;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDetailsDTO;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;

import java.util.List;

public interface CustomerServiceService extends BaseService<CustomerService, Long> {
    public List<CustomerServiceDetailsDTO> getAllCustomerServices();
}
