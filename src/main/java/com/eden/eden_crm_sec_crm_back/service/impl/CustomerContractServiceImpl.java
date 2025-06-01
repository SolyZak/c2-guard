package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.Currency;
import com.eden.eden_crm_sec_crm_back.clients.dto.SecurityCompanyData;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractRowDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractServiceDetailsData;
import com.eden.eden_crm_sec_crm_back.dto.response.DistributedOperationSite;
import com.eden.eden_crm_sec_crm_back.dto.response.DistributedOperationSiteDetail;
import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.*;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.ServiceDetailsRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerContractServiceImpl implements CustomerContractService {
    private final CustomerContractRepository customerContractRepository;
    private final com.eden.eden_crm_sec_crm_back.service.CustomerService customerService;
    private final CustomerContractMapper contractMapper;
    private final ServiceDetailsRepository serviceDetailsRepository;
    private final LKCustomerContractServiceRepository contractServiceRepository;
    private final OrgUnitClient orgUnitClient;
    private final SiteDistributionRepository siteDistributionRepository;
    private final CustomerSiteRepository customerSiteRepository;
    private final LKCustomerContractOperationServiceRepository contractOperationServiceRepository;
    private final CustomerSiteMapper customerSiteMapper;

    @Override
    @Transactional
    public String createAgreement(AddContractDto dto) {
        Customer customer = customerService.getLoggedInCustomer();
        SecurityCompanyData securityCompanyData = null;
        try {
            securityCompanyData = orgUnitClient.getSecurityCompanyDetails(dto.getSecurityCompanyId());
        } catch (Exception e) {
            log.error("Can`t get security company info from org unit service, error: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("security-company")}), HttpStatus.NOT_FOUND);
        }
        Currency currency = null;
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
        Customer customer = customerService.getLoggedInCustomer();
        Page<CustomerContract> contracts = customerContractRepository.paginateByCustomer(customer.getId(), search, from, to, pageable);
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
        Customer customer = customerService.getLoggedInCustomer();
        return customerContractRepository.listByCustomerIdAndStatus(
                        customer.getId(), List.of(ContractStatus.SAVED, ContractStatus.ON_DISTRIBUTE)
                )
                .stream()
                .map(contractMapper::toContractRowDto)
                .toList();
    }

    @Override
    public List<ContractServiceDetailsData> contractServicesList(Long contractId) {
        Customer customer = customerService.getLoggedInCustomer();
        customerContractRepository.findByIdAndCustomerId(contractId, customer.getId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
        );
        return contractServiceRepository.getContractServices(contractId)
                .stream().map(contractMapper::toContractServiceDetailsData).toList();
    }

    @Override
    @Transactional
    public String contractDistribute(Long contractId, SiteDistributionDto dto) {
        Customer customer = customerService.getLoggedInCustomer();
        CustomerContract contract = customerContractRepository.findWithDetailsByIdAndCustomerId(contractId, customer.getId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
        );
        LKCustomerContractService service = serviceFromContractAsDto(dto.getLkCustomerContractServiceId(), contract);

        validateRequestQntVsContract(dto, contract, service);
        validateDaysVsContractDays(dto, service);

        CustomerSite customerSite = customerSiteRepository.findByIdAndCustomerId(dto.getSiteId(), customer.getId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("operation-site")}), HttpStatus.NOT_FOUND)
        );

        SiteDistribution siteDistribution = new SiteDistribution();
        siteDistribution.setCustomerContract(contract);
        siteDistribution.setSite(customerSite);
        siteDistribution.setActivities(dto.getActivities());
        siteDistribution.setLkCustomerContractService(service);
        siteDistributionRepository.save(siteDistribution);

        List<LKCustomerContractOperationService> operationServices = new ArrayList<>();

        dto.getOperationServices().forEach(lkCustomerContractOperationServiceDto -> {
            LKCustomerContractOperationService lkCustomerContractOperationService = new LKCustomerContractOperationService();
            lkCustomerContractOperationService.setSiteDistribution(siteDistribution);
            lkCustomerContractOperationService.setDays(lkCustomerContractOperationServiceDto.getDays());
            lkCustomerContractOperationService.setQuantity(lkCustomerContractOperationServiceDto.getQuantity());
            lkCustomerContractOperationService.setFromTime(lkCustomerContractOperationServiceDto.getFromTime());
            lkCustomerContractOperationService.setToTime(lkCustomerContractOperationServiceDto.getFromTime().plusHours(service.getCustomerService().getHours()));

            operationServices.add(lkCustomerContractOperationService);
        });

        Long serviceDistributedQnt = dto.getOperationServices().stream().mapToLong(LKCustomerContractOperationServiceDto::getQuantity).sum();
        service.setDistributedQuantity(serviceDistributedQnt);
        contractServiceRepository.save(service);

        contractOperationServiceRepository.saveAll(operationServices);

        contract.setStatus(getContractNewStatus(contractId, service.getId(), service.getQuantity(), serviceDistributedQnt));
        customerContractRepository.save(contract);

        return MessageUtil.getMessage("contract.distributed");
    }

    @Override
    public List<ContractRowDto> listAllMyContracts() {
        return customerContractRepository.listByCustomerId(customerService.getLoggedInCustomer().getId())
                .stream()
                .map(contractMapper::toContractRowDto).toList();
    }

    @Override
    public List<GeneralDropdown> availableOperationSitesList(Long contractId) {
        Customer customer = customerService.getLoggedInCustomer();
        CustomerContract contract = customerContractRepository.findByIdAndCustomerId(contractId, customer.getId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
        );
        return customerSiteRepository.findAvailableSitesForContract(
                        customer.getId(),
                        contractId,
                        contract.getStartAgreementDate(),
                        contract.getEndAgreementDate()
                )
                .stream()
                .map(customerSiteMapper::toDropdown)
                .toList();
    }

    @Override
    public List<DistributedOperationSite> distributedOperationSites(Long contractId, Long lkCustomerContractServiceId) {
        Customer customer = customerService.getLoggedInCustomer();
        customerContractRepository.findByIdAndCustomerId(contractId, customer.getId()).orElseThrow(
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

    private LKCustomerContractService serviceFromContractAsDto(Long id, CustomerContract contract) {
        return contract.getCustomerContractServices()
                .stream().filter(
                        e -> e.getId().equals(id)
                )
                .findFirst()
                .orElseThrow(
                        () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("service")}), HttpStatus.NOT_FOUND)
                );
    }

    private void validateDaysVsContractDays(SiteDistributionDto dto, LKCustomerContractService service) {
        for (LKCustomerContractOperationServiceDto operationService : dto.getOperationServices()) {
            int daysSize = operationService.getDays().size();
            if (!service.getCustomerService().getDays().equals((long) daysSize)) {
                throw new BusinessException(MessageUtil.getMessage("contract.distribution.days-missing"), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void validateRequestQntVsContract(SiteDistributionDto dto, CustomerContract contract, LKCustomerContractService service) {
        long qntSum = dto.getOperationServices().stream().mapToLong(LKCustomerContractOperationServiceDto::getQuantity).sum();
        long existsQntSum = contract.getSiteDistributions().stream()
                .filter(
                        existsSite -> existsSite.getSite().getId().equals(dto.getSiteId()) &&
                                existsSite.getLkCustomerContractService().getId().equals(dto.getLkCustomerContractServiceId())
                )
                .flatMap(existsSite -> existsSite.getOperationServices().stream())
                .mapToLong(LKCustomerContractOperationService::getQuantity)
                .sum();

        if (service.getQuantity() < qntSum + existsQntSum) {
            throw new BusinessException(MessageUtil.getMessage("contract.distribution.you-exceed-quantity"), HttpStatus.BAD_REQUEST);
        }
    }

    private ContractStatus getContractNewStatus(
            Long contractId,
            Long serviceId,
            Long serviceQnt,
            Long serviceDistributedQnt
    ) {

        Long qnt = contractServiceRepository.sumQnty(contractId, serviceId);
        Long distributedQnt = contractServiceRepository.sumDistributedQnty(contractId, serviceId);

        if (qnt.equals(distributedQnt) && serviceQnt.equals(serviceDistributedQnt)) {
            return ContractStatus.DISTRIBUTED;
        }
        return ContractStatus.ON_DISTRIBUTE;
    }
}
