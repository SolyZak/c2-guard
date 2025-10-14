package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerSiteRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSitePremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerSiteResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.PremiseNotProvided;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerSiteMapper;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerSiteServiceImpl implements CustomerSiteService {

    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerRepository customerRepository;
    private final CustomerSiteMapper customerSiteMapper;
    private final SiteDistributionRepository siteDistributionRepository;
    private final PremiseRepository premiseRepository;
    private final Utils utils;

    private CustomerSiteMapper mapper;

    @Override
    public CustomerSiteResponseDto addCustomerSite(CustomerSiteRequestDto requestDto) {
        Long customerId = getLoggedInCustomerId();
        Customer customer = customerRepository.findById(customerId).orElseThrow(UserNotProvided::new);
        Premise premise = premiseRepository.findById(requestDto.getPremiseId()).orElseThrow(PremiseNotProvided::new);

        CustomerSite site = customerSiteMapper.toEntity(requestDto);
        site.setCustomer(customer);
        site.setTimezone(customer.getTimezone());
        site.setPremise(premise);
        customerSiteRepository.save(site);

        return customerSiteMapper.fromEntity(site);
    }

    @Override
    public void updateCustomerSite(Long id, UpdateCustomerSiteRequestDto requestDto) {
        Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);
        Premise premise = premiseRepository.findById(requestDto.getPremiseId()).orElseThrow(PremiseNotProvided::new);

        CustomerSite site = findOne(id, customer.getId());

        customerSiteMapper.updateEntityFromDto(requestDto, site);
        site.setTimezone(customer.getTimezone());
        site.setPremise(premise);
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

    @Override
    public PaginateResponse<CustomerSitePremiseResponseDto> getSitesPaginated(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<CustomerSite> resultPage = customerSiteRepository.searchByCustomerSiteNameAndPremiseName(search,pageable);
        List<CustomerSitePremiseResponseDto> customerSiteResponseDtos = new ArrayList<>();
        if (resultPage.getContent() != null) {
            for(CustomerSite cs : resultPage.getContent()) {
                CustomerSitePremiseResponseDto dto = new CustomerSitePremiseResponseDto(cs.getId(),cs.getName(),cs.getLatitude(),cs.getLongitude(),cs.getTolerance(),cs.getPremise() != null ? cs.getPremise().getName() : "");
                customerSiteResponseDtos.add(dto);
            }
        }
        return new PaginateResponse<>(
                customerSiteResponseDtos,
                page,
                size,
                resultPage.getTotalElements(),
                (long) resultPage.getTotalPages()
        );
    }

    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }
}
