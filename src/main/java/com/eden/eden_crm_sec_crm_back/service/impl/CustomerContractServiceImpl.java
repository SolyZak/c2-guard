package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.Currency;
import com.eden.eden_crm_sec_crm_back.clients.dto.SecurityCompanyData;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.mapper.*;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.ServiceDetailsRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import com.eden.eden_crm_sec_crm_back.utils.DateUtils;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerContractServiceImpl implements CustomerContractService {
    private final CustomerContractRepository customerContractRepository;
    private final CustomerRepository customerRepository;
    private final CustomerContractMapper contractMapper;
    private final ServiceDetailsRepository serviceDetailsRepository;
    private final LKCustomerContractServiceRepository contractServiceRepository;
    private final OrgUnitClient orgUnitClient;
    private final SiteDistributionRepository siteDistributionRepository;
    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerSiteMapper customerSiteMapper;
    private final Utils utils;

    @Override
    @Transactional
    public String createAgreement(AddContractDto dto) {
        Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);
        Optional<CustomerContract> agreementNumberExists = customerContractRepository.findByAgreementNumber(dto.getAgreementNumber());
        if (agreementNumberExists.isPresent()) {
            throw new BusinessException(MessageUtil.getMessage("contract.number.already-exists"), HttpStatus.BAD_REQUEST);
        }
        SecurityCompanyData securityCompanyData;
        try {
            securityCompanyData = orgUnitClient.getSecurityCompanyDetails(dto.getSecurityCompanyId());
        } catch (Exception e) {
            log.error("Can`t get security company info from org unit service, error: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("security-company")}), HttpStatus.NOT_FOUND);
        }
        Currency currency;
        try {
            currency = orgUnitClient.getCurrencyDetails(dto.getCurrency());
        } catch (Exception e) {
            log.error("Can`t get currency info from org unit service, error: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("currency")}), HttpStatus.NOT_FOUND);
        }
        CustomerContract contract = contractMapper.toEntity(dto);
        contract.setCustomer(customer);
        contract.setStatus(ContractStatus.SAVED);
        contract.setSecurityCompanyName(securityCompanyData != null ? securityCompanyData.name() : "");
        contract.setCurrencyName(currency != null ? currency.name() : "");
        contract.setCurrencyCode(currency != null ? currency.code() : "");

        List<LKCustomerContractService> contractServices = new ArrayList<>();
        Map<Long, ServiceDetails> serviceDetailsMap = serviceDetailsRepository
                .findAllById(dto.getServices().stream()
                        .map(AddContractServiceDto::getServiceDetailsId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(ServiceDetails::getId, Function.identity()));
        dto.getServices().forEach(serviceDetailDto -> {
            ServiceDetails serviceDetails = serviceDetailsMap.get(serviceDetailDto.getServiceDetailsId());
            if (serviceDetails != null) {
                LKCustomerContractService contractService = contractMapper.toEntity(serviceDetailDto);
                contractService.setCustomerService(serviceDetails);
                contractService.setCustomerContract(contract);
                contractServices.add(contractService);
            }
        });

        contract.setCustomerContractServices(contractServices);
        customerContractRepository.save(contract);
        return MessageUtil.getMessage("entity.created", new Object[]{MessageUtil.getMessage("contract")});
    }

    @Override
    public PaginateResponse<ContractRowDto> paginateMyContracts(String search, LocalDate from, LocalDate to, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<CustomerContract> contracts = customerContractRepository.paginateByCustomer(getLoggedInCustomerId(), search, from, to, pageable);
        return new PaginateResponse<>(
                contracts.getContent().stream().map(contractMapper::toContractRowDto).toList(),
                page,
                size,
                contracts.getTotalElements(),
                (long) contracts.getTotalPages()
        );
    }

    @Override
    public List<ContractRowDto> listMyDraftedContracts() {
        return customerContractRepository.listByCustomerIdAndStatus(
                        getLoggedInCustomerId(), List.of(ContractStatus.SAVED, ContractStatus.ON_DISTRIBUTE)
                )
                .stream()
                .map(contractMapper::toContractRowDto)
                .toList();
    }

    @Override
    public List<ContractServiceDetailsData> contractServicesList(Long contractId) {
        customerContractRepository.findByIdAndCustomerId(contractId, getLoggedInCustomerId())
                .orElseThrow(
                        () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
                );
        List<ContractServiceDetailsData> result = contractServiceRepository.getContractNotFullyDistributedServices(contractId)
                .stream().map(contractMapper::toContractServiceDetailsData).toList();
        for (ContractServiceDetailsData contractServiceDetailsData : result) {
            if (contractServiceDetailsData.getDistributedQuantity() == null || contractServiceDetailsData.getDistributedQuantity().equals(0L))
                contractServiceDetailsData.setDistributed(false);
            else
                contractServiceDetailsData.setDistributed(true);
        }
        return result;
    }

    @Override
    public List<ContractRowDto> listAllMyContracts() {
        return customerContractRepository.listByCustomerId(getLoggedInCustomerId())
                .stream()
                .map(contractMapper::toContractRowDto).toList();
    }

    @Override
    public List<GeneralDropdown> availableOperationSitesList(Long contractId) {

        customerContractRepository.findByIdAndCustomerId(contractId, getLoggedInCustomerId())
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("entity.not-found",
                                new Object[]{MessageUtil.getMessage("contract")}),
                        HttpStatus.NOT_FOUND));

        return customerSiteRepository.findCustomerSitesForDropdown(getLoggedInCustomerId())   // <<--
                .stream()
                .map(customerSiteMapper::toDropdown)
                .toList();
    }

//@Override
//public List<GeneralDropdown> availableOperationSitesList(Long contractId) {
//
//    // make sure the contract really belongs to the logged-in customer
//    customerContractRepository
//            .findByIdAndCustomerId(contractId, getLoggedInCustomerId())
//            .orElseThrow(() -> new BusinessException(
//                    MessageUtil.getMessage("entity.not-found",
//                            new Object[]{MessageUtil.getMessage("contract")}),
//                    HttpStatus.NOT_FOUND));
//
//    return customerSiteRepository
//            .findOperationSitesForDropdown(contractId, getLoggedInCustomerId())
//            .stream()
//            .map(customerSiteMapper::toDropdown)
//            .toList();
//}

    @Override
    public List<DistributedOperationSite> distributedOperationSites(Long contractId, Long lkCustomerContractServiceId) {
        customerContractRepository.findByIdAndCustomerId(contractId, getLoggedInCustomerId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
        );

        return siteDistributionRepository.findByContractAndLKCustomerService(contractId, lkCustomerContractServiceId)
                .stream().map(sd -> {
                    AtomicReference<Long> allQuantities = new AtomicReference<>(0L);
                    List<DistributedOperationSiteDetail> details = sd.getOperationServices().stream().map(os -> {
                        allQuantities.updateAndGet(v -> v + os.getQuantity());
                        return DistributedOperationSiteDetail.builder()
                                .days(os.getDays())
                                .quantity(os.getQuantity())
                                .fromTime(os.getFromTime())
                                .toTime(os.getToTime())
                                .build();
                    }).toList();
                    return DistributedOperationSite.builder()
                            .operationSiteId(sd.getSite().getId())
                            .operationSiteName(sd.getSite().getName())
                            .quantity(allQuantities.get())
                            .activities(sd.getActivities())
                            .details(details)
                            .build();
                })
                .toList();
    }

    @Override
    public ContractDetailsData getCustomerContractDetails(Long contractId) {
        Long customerId = getLoggedInCustomerId();

        CustomerContract contract = customerContractRepository
                .findWithServicesByIdAndCustomerId(contractId, customerId)
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}),
                        HttpStatus.NOT_FOUND
                ));
        Customer customer = contract.getCustomer();

        List<SiteDistribution> siteDistributions = siteDistributionRepository.findDistributionsByContractId(contractId);

        ContractDetailsData detailsData = contractMapper.fromEntity(contract);

        // Group SiteDistributions by Service ID
        Map<Long, List<SiteDistribution>> distributionsByServiceId = siteDistributions.stream()
                .collect(Collectors.groupingBy(sd -> sd.getLkCustomerContractService().getId()));

        List<ContractDetailsData.ContractServiceDetails> services = contract.getCustomerContractServices()
                .stream()
                .map(ccs -> {
                    ContractDetailsData.ContractServiceDetails serviceDetails = contractMapper.fromEntity(ccs);

                    List<ContractDetailsData.ContractServiceDetails.ContractServiceDistributionsData> distributionDataList = Optional
                            .ofNullable(distributionsByServiceId.get(ccs.getId()))
                            .orElse(Collections.emptyList())
                            .stream()
                            .collect(Collectors.groupingBy(sd -> sd.getSite().getId()))
                            .values()
                            .stream()
                            .map(siteGroup -> {
                                AtomicReference<Long> totalQnt = new AtomicReference<>(0L);
                                List<ContractDetailsData.ContractServiceDetails.ContractServiceDistributionsData.ContractOperationServiceDetails> operationServiceDetails =
                                        siteGroup.stream()
                                                .flatMap(sd -> sd.getOperationServices().stream())
                                                .map(os -> {
                                                    totalQnt.updateAndGet(v -> v + os.getQuantity());
                                                    LocalTime from = DateUtils.toLocalTime(customer.getTimezone(), os.getFromTime());
                                                    LocalTime to = DateUtils.toLocalTime(customer.getTimezone(), os.getToTime());
                                                    return contractMapper.fromEntity(os, from, to);
                                                })
                                                .toList();

                                SiteDistribution representative = siteGroup.getFirst(); // all have same site
                                return ContractDetailsData.ContractServiceDetails.ContractServiceDistributionsData.builder()
                                        .activities(representative.getActivities())
                                        .operationSiteName(representative.getSite().getName())
                                        .quantity(totalQnt.get())
                                        .operationServices(operationServiceDetails)
                                        .build();
                            })
                            .toList();

                    serviceDetails.setDistributions(distributionDataList);
                    return serviceDetails;
                })
                .toList();

        detailsData.setServices(services);
        return detailsData;
    }

    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }

}
