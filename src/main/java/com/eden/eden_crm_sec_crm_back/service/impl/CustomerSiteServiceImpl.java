package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerSiteMapper;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerSiteServiceImpl implements CustomerSiteService {

    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerRepository customerRepository;
    private final CustomerSiteMapper customerSiteMapper;
    private final SiteDistributionRepository siteDistributionRepository;
    private final Utils utils;

    @Override
    public CustomerSiteResponseDto addCustomerSite(CustomerSiteRequestDto requestDto) {
        Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);

        CustomerSite site = customerSiteMapper.toEntity(requestDto);
        site.setCustomer(customer);
        site.setTimezone(customer.getTimezone());
        customerSiteRepository.save(site);

        return customerSiteMapper.fromEntity(site);
    }

    @Override
    public void updateCustomerSite(Long id, UpdateCustomerSiteRequestDto requestDto) {
        Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);

        CustomerSite site = findOne(id, customer.getId());

        customerSiteMapper.updateEntityFromDto(requestDto, site);
        site.setTimezone(customer.getTimezone());
        customerSiteRepository.save(site);
    }

    @Override
    public List<CustomerSiteResponseDto> getSitesForCustomer() {
        return customerSiteRepository.findByCustomerId(getLoggedInCustomerId()).stream().map(customerSiteMapper::fromEntity).toList();
    }

    @Override
    public List<CustomerSiteResponseDto> getSitesForCustomer(Long customerId) {
        return customerSiteRepository.findByCustomerId(customerId).stream().map(customerSiteMapper::fromEntity).toList();
    }

    @Override
    public String deleteSiteForCustomer(Long id) {
        CustomerSite site = findOne(id, getLoggedInCustomerId());

        if (siteDistributionRepository.existsBySiteId(site.getId())) {
            throw new BusinessException(MessageUtil.getMessage("site-has-contract"), HttpStatus.BAD_REQUEST);
        }
        customerSiteRepository.delete(site);

        return MessageUtil.getMessage("customer-site.deleted");
    }

    @Override
    public CustomerSite findOne(Long id, Long customerId) {
        return customerSiteRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("not-found"), HttpStatus.NOT_FOUND));
    }

    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }
}
