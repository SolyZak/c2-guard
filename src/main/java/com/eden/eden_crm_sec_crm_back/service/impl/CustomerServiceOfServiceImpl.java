package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDataDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDetailsDropdownDto;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerServiceMapper;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.service.CustomerServiceService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceOfServiceImpl implements CustomerServiceService {

    private final CustomerServiceRepository customerServiceRepository;
    private final CustomerService customerService;
    private final CustomerServiceMapper serviceMapper;

    @Override
    public PaginateResponse<ServiceDataDto> paginateMyServices(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.eden.eden_crm_sec_crm_back.models.CustomerService> services = customerServiceRepository.servicesByCustomer(
                Utils.getLoggedInCustomerId(), pageable
        );
        return new PaginateResponse<>(
                services.getContent().stream().map(serviceMapper::toServiceDataDto).toList(),
                page,
                size,
                services.getTotalElements(),
                (long) services.getTotalPages()
        );
    }

    @Override
    public List<ServiceDetailsDropdownDto> allMyServiceDetails() {
        List<com.eden.eden_crm_sec_crm_back.models.CustomerService> services = customerServiceRepository.servicesByCustomer(
                Utils.getLoggedInCustomerId()
        );
        return services.stream().flatMap(s -> s.getServiceDetails().stream()).map(serviceMapper::toServiceDetailsDropdownDto).toList();
    }

    @Override
    public String create(AddServiceDto dto) {
        Customer customer = customerService.getLoggedInCustomer();
        com.eden.eden_crm_sec_crm_back.models.CustomerService service = serviceMapper.toEntity(dto);
        service.setServiceDetails(dto.getDetails().stream().map(d -> serviceMapper.toEntity(d, service)).toList());
        service.setCustomer(customer);
        customerServiceRepository.save(service);
        return MessageUtil.getMessage("entity.created", new Object[]{MessageUtil.getMessage("service")});
    }
}
