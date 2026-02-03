package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.SiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractOperationServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractDistributionResponseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.DistributionTimesWithQuantity;
import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.models.projections.DistributionTimesProjection;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.ContractDistributeService;
import com.eden.eden_crm_sec_crm_back.utils.DateUtils;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractDistributeServiceImpl implements ContractDistributeService {

    private final CustomerRepository customerRepository;
    private final CustomerContractRepository customerContractRepository;
    private final CustomerSiteRepository customerSiteRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final LKCustomerContractOperationServiceRepository contractOperationServiceRepository;
    private final LKCustomerContractServiceRepository contractServiceRepository;
    private final Utils utils;

    @Override
    @Transactional
    public ContractDistributionResponseDto contractDistribute(Long contractId, Long serviceId, List<SiteDistributionDto> listDto) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        CustomerContract contract = customerContractRepository.findWithDetailsByIdAndCustomerId(contractId, customer.getId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
        );
        LKCustomerContractService service = serviceFromContractAsDto(serviceId, contract);
        ServiceDetails serviceDetails = service.getCustomerService();

        final Map<Long, CustomerSite> customerSites = getCustomerSiteMap(listDto, customer);

        // validate the distributed quantity is equal to service quantity
        Long distributedQnt = listDto.stream().flatMap(sd -> sd.getOperationServices().stream())
                .mapToLong(LKCustomerContractOperationServiceDto::getQuantity)
                .sum();
        if (!service.getQuantity().equals(distributedQnt)) {
            throw new BusinessException(MessageUtil.getMessage("service-must-fully-distributed"), HttpStatus.NOT_FOUND);
        }

        // validate the distributed days are equals to days count in service
        if (
                listDto.stream().flatMap(dto -> dto.getOperationServices().stream()).anyMatch(os -> os.getDays().size() != serviceDetails.getDays())
        ) throw new BusinessException(MessageUtil.getMessage("service-days-must-fully-distributed"), HttpStatus.NOT_FOUND);

        // create site distributions
        List<LKCustomerContractOperationService> operationServices = new ArrayList<>();
        AtomicLong distributionId = new AtomicLong();
        listDto.forEach(dto -> {
            CustomerSite customerSite = customerSites.get(dto.getSiteId());
            SiteDistribution siteDistribution = new SiteDistribution();
            siteDistribution.setCustomerContract(contract);
            siteDistribution.setSite(customerSite);
            siteDistribution.setActivities(dto.getActivities());
            siteDistribution.setLkCustomerContractService(service);
            SiteDistribution temp = siteDistributionRepository.save(siteDistribution);
            distributionId.set(temp.getId());

            dto.getOperationServices().forEach(lkCustomerContractOperationServiceDto -> {
                OffsetTime[] times = getTimes(
                        lkCustomerContractOperationServiceDto.getFromTime(),
                        customer.getTimezone(),
                        service.getCustomerService().getHours()
                );
                LKCustomerContractOperationService lkCustomerContractOperationService = new LKCustomerContractOperationService();
                lkCustomerContractOperationService.setSiteDistribution(siteDistribution);
                lkCustomerContractOperationService.setDays(lkCustomerContractOperationServiceDto.getDays());
                lkCustomerContractOperationService.setQuantity(lkCustomerContractOperationServiceDto.getQuantity());
                lkCustomerContractOperationService.setFromTime(times[0]);
                lkCustomerContractOperationService.setToTime(times[1]);

                operationServices.add(lkCustomerContractOperationService);
            });
        });

        contractOperationServiceRepository.saveAll(operationServices);
        service.setDistributedQuantity(service.getQuantity());
        contractServiceRepository.save(service);

        return new ContractDistributionResponseDto(MessageUtil.getMessage("contract-service.distributed"), distributionId.get());
    }

    @Override
    public List<DistributionTimesProjection> getAllStartEndTimesForDistribution(Long distributionId) {
        return contractOperationServiceRepository.findAllOffsetStartAndEndByDistributionId(distributionId);
    }

    @Override
    public List<DistributionTimesWithQuantity> getAllStartEndTimesForDistributionWithQuantity(Long contractId, Long serviceId, Long siteId) {
        Optional<SiteDistribution> siteDistributionOptional = siteDistributionRepository.findBySiteIdAndContractIdAndServiceId(siteId, contractId, serviceId);
        if (!siteDistributionOptional.isPresent()) {
            throw new BusinessException(MessageUtil.getMessage("entity.not-found"), HttpStatus.NOT_FOUND);
        }
        List<DistributionTimesWithQuantity> result = new ArrayList<>();
        CustomTimezone customerTimezone = siteDistributionOptional.get().getSite().getCustomer().getTimezone();
        List<LKCustomerContractOperationService> operationServices = siteDistributionOptional.get().getOperationServices();
        if (operationServices != null) {
            for (LKCustomerContractOperationService service : operationServices) {
                for (int i = 0; i < service.getQuantity(); i++) {
                    result.add(new DistributionTimesWithQuantity(
                            DateUtils.withTimeZone(customerTimezone, service.getFromTime()),
                            DateUtils.withTimeZone(customerTimezone, service.getToTime()),
                            service.getId() + "_" + i,
                            service.getId()
                    ));
                }
            }
        }
        return result;
    }

    private Map<Long, CustomerSite> getCustomerSiteMap(List<SiteDistributionDto> listDto, Customer customer) {
        // make sure the requested sites for distribute are new and not have been distributed before
        List<Long> siteIds = listDto.stream().map(SiteDistributionDto::getSiteId).toList();
        Map<Long, CustomerSite> customerSites = customerSiteRepository.listByIdAndCustomerId(siteIds, customer.getId())
                .stream().collect(Collectors.toMap(CustomerSite::getId, Function.identity()));

        if (siteIds.size() != customerSites.size()) {
            throw new BusinessException(MessageUtil.getMessage("distribute-sites-not-accurate"), HttpStatus.NOT_FOUND);
        }
        return customerSites;
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

    private OffsetTime[] getTimes(
            LocalTime rawFromTime,
            CustomTimezone customerTimezone,
            Long serviceHours
    ) {
        ZoneId customerZone = DateUtils.getTimeWithTimezone(customerTimezone);

        ZonedDateTime fromZoned = rawFromTime.atDate(LocalDate.now()).atZone(customerZone);
        ZonedDateTime toZoned = fromZoned.plusHours(serviceHours);

        return new OffsetTime[] {
                fromZoned.toOffsetDateTime().toOffsetTime(),
                toZoned.toOffsetDateTime().toOffsetTime()
        };
    }
}
