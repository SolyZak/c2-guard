package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionWorkingPeriodDto;
import com.eden.eden_crm_sec_crm_back.enums.AttendStatus;
import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerContractMapper;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerMapper;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerSiteMapper;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationRule;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerSiteRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkforceServiceImpl implements WorkforceService {

    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerContractRepository contractRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final CustomerSiteMapper customerSiteMapper;
    private final CustomerMapper customerMapper;
    private final CustomerContractMapper customerContractMapper;
    private final OrgUnitClient orgUnitClient;

    @Override
    public List<GeneralDropdown> customersDropdown() {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        return contractRepository.listCustomerBySecurityCompanyIdAndActiveToday(
                        workforceFullDataDto.securityCompany().id(), LocalDate.now()
                )
                .stream().map(customerMapper::toDropdown)
                .toList();
    }

    @Override
    public List<GeneralDropdown> customersContractDropdown(Long customerId) {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        return contractRepository.listByCustomerIdSecurityCompanyIdAndActiveToday(
                        workforceFullDataDto.securityCompany().id(), customerId, LocalDate.now()
                )
                .stream().map(customerContractMapper::toDropdown)
                .toList();
    }

    @Override
    public List<WorkforceSiteDistributionDto> customersContractOperationSitesDropdown(Long customerId, Long contractId) {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        CustomerContract contract = contractRepository.findByCustomerIdSecurityCompanyIdAndActiveToday(
                workforceFullDataDto.securityCompany().id(), customerId, contractId, LocalDate.now()
        ).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.NOT_FOUND)
        );
        WeekDaysEnum weekDaysEnum = Utils.getTodayWeekDayEnum();

        Map<CustomerSite, List<SiteDistribution>> groupedBySite = contract.getSiteDistributions().stream().filter(sd ->
                        sd.getOperationServices().stream().anyMatch(os -> os.getDays().contains(weekDaysEnum))
                )
                .collect(Collectors.groupingBy(SiteDistribution::getSite));
        List<WorkforceSiteDistributionDto> workforceSiteDistributions = new ArrayList<>();

        ContractOperationRule contractOperationRule = contract.getCustomerAgreement();

        groupedBySite.forEach((site, distributions) -> {
            WorkforceSiteDistributionDto workforceSiteDistributionDto = WorkforceSiteDistributionDto.builder()
                    .id(site.getId())
                    .name(site.getName())
                    .contractId(contractId)
                    .contractName(contract.getAgreementName())
                    .customerId(contract.getCustomer().getId())
                    .customerName(contract.getCustomer().getName())
                    .latitude(site.getLatitude())
                    .longitude(site.getLongitude())
                    .tolerance(site.getTolerance())
                    .services(distributions.stream()
                            .filter(d -> d.getLkCustomerContractService().getCustomerService().getCustomerService().getUnit().equals(UnitEnum.PERSON))
                            .map(d -> {
                                ServiceDetails details = d.getLkCustomerContractService().getCustomerService();
                                return WorkforceSiteDistributionServiceDto.builder()
                                        .id(d.getLkCustomerContractService().getId())
                                        .name(details.getCustomerService().getServiceName())
                                        .hours(details.getHours())
                                        .days(details.getDays())
                                        .unit(UnitEnum.PERSON)
                                        .qnt(d.getLkCustomerContractService().getQuantity())
                                        .periods(
                                                d.getOperationServices().stream()
                                                        .map(os -> WorkforceSiteDistributionWorkingPeriodDto.builder()
                                                                .id(os.getId())
                                                                .fromTime(os.getFromTime())
                                                                .toTime(os.getToTime())
                                                                .isWorking(isWorkingPeriod(contractOperationRule, os))
                                                                .checkInStatus(checkInStatus(contractOperationRule, os))
                                                                .checkOutStatus(checkOutStatus(contractOperationRule, os))
                                                                .presenceMode(contractOperationRule.getPresenceMode())
                                                                .build())
                                                        .toList()
                                        )
                                        .build();
                            }).toList()
                    )
                    .build();
            workforceSiteDistributions.add(workforceSiteDistributionDto);
        });
        return workforceSiteDistributions;
    }

    @Override
    public List<GeneralDropdown> operationSitesDropdown() {
        return customerSiteRepository.findAll().stream().map(customerSiteMapper::toDropdown).toList();
    }

    @Override
    public WorkforceSiteDistributionDto operationSiteServicesDropdown(Long id) {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        List<SiteDistribution> distributions = siteDistributionRepository.listForSecurityCompanyActiveTodayAndSiteId(
                workforceFullDataDto.securityCompany().id(), id, LocalDate.now()
        );
        if (distributions.isEmpty()) {
            throw new BusinessException(MessageUtil.getMessage("not-your-working-period"), HttpStatus.BAD_REQUEST);
        }
        WeekDaysEnum weekDaysEnum = Utils.getTodayWeekDayEnum();
        CustomerSite site = distributions.getFirst().getSite();
        CustomerContract contract = distributions.getFirst().getCustomerContract();
        return WorkforceSiteDistributionDto.builder()
                .id(site.getId())
                .name(site.getName())
                .contractId(contract.getId())
                .contractName(contract.getAgreementName())
                .customerId(contract.getCustomer().getId())
                .customerName(contract.getCustomer().getName())
                .latitude(site.getLatitude())
                .longitude(site.getLongitude())
                .tolerance(site.getTolerance())
                .services(distributions.stream()
                        .filter(d -> d.getLkCustomerContractService().getCustomerService().getCustomerService().getUnit().equals(UnitEnum.PERSON))
                        .map(d -> {
                            ServiceDetails details = d.getLkCustomerContractService().getCustomerService();
                            ContractOperationRule contractOperationRule = d.getCustomerContract().getCustomerAgreement();
                            return WorkforceSiteDistributionServiceDto.builder()
                                    .id(d.getLkCustomerContractService().getId())
                                    .name(details.getCustomerService().getServiceName())
                                    .hours(details.getHours())
                                    .days(details.getDays())
                                    .unit(UnitEnum.PERSON)
                                    .qnt(d.getLkCustomerContractService().getQuantity())
                                    .periods(
                                            d.getOperationServices().stream()
                                                    .filter(os -> os.getDays().contains(weekDaysEnum))
                                                    .map(os -> WorkforceSiteDistributionWorkingPeriodDto.builder()
                                                            .id(os.getId())
                                                            .fromTime(os.getFromTime())
                                                            .toTime(os.getToTime())
                                                            .isWorking(isWorkingPeriod(contractOperationRule, os))
                                                            .checkInStatus(checkInStatus(contractOperationRule, os))
                                                            .checkOutStatus(checkOutStatus(contractOperationRule, os))
                                                            .presenceMode(contractOperationRule.getPresenceMode())
                                                            .build()
                                                    )
                                                    .toList()
                                    )
                                    .build();
                        }).toList()
                )
                .build();
    }

    private WorkforceFullDataDto getLoggedInWorkforce() {
        Integer workforceId = Utils.getLoggedInWorkforceId().intValue();
        try {
            // int, for better performance we can cache result here for some time like 15 minutes
            WorkforceFullDataDto workforceFullDataDto = orgUnitClient.getWorkforceDetails(workforceId);
            if (workforceFullDataDto == null) throw new Exception();
            return workforceFullDataDto;
        } catch (Exception e) {
            log.error("WorkforceServiceImpl::customersDropdown, Error while try to get workforce details from org unit, error: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }

    private Boolean isWorkingPeriod(
            ContractOperationRule contractOperationRule,
            LKCustomerContractOperationService operationService
    ) {
        LocalDateTime from = LocalDateTime.of(LocalDate.now(), operationService.getFromTime())
                .minusMinutes(contractOperationRule.getCheckInBeforeMinutes());
        LocalDateTime to = LocalDateTime.of(LocalDate.now(), operationService.getToTime());
        return LocalDateTime.now().isBefore(to) && LocalDateTime.now().isAfter(from);
    }

    private AttendStatus checkInStatus(
            ContractOperationRule contractOperationRule,
            LKCustomerContractOperationService operationService
    ) {
        LocalDateTime earlyFrom = LocalDateTime.of(LocalDate.now(), operationService.getFromTime())
                .minusMinutes(contractOperationRule.getCheckInBeforeMinutes());
        LocalDateTime earlyTo = LocalDateTime.of(LocalDate.now(), operationService.getFromTime());

        if (
                (LocalDateTime.now().isAfter(earlyFrom) || LocalDateTime.now().isEqual(earlyFrom))
                        && (LocalDateTime.now().isBefore(earlyTo) || LocalDateTime.now().isEqual(earlyTo))
        ) return AttendStatus.CHECK_IN_EARLY;

        LocalDateTime inTimeFrom = LocalDateTime.of(LocalDate.now(), operationService.getFromTime());
        LocalDateTime inTimeTo = LocalDateTime.of(LocalDate.now(), operationService.getFromTime())
                .plusMinutes(contractOperationRule.getCheckInAfterMinutes());
        if (
                (LocalDateTime.now().isAfter(inTimeFrom) || LocalDateTime.now().isEqual(inTimeFrom))
                        && (LocalDateTime.now().isBefore(inTimeTo) || LocalDateTime.now().isEqual(inTimeTo))
        ) return AttendStatus.CHECK_IN_IN_TIME;
        return AttendStatus.CHECK_IN_LATE;
    }

    private AttendStatus checkOutStatus(
            ContractOperationRule contractOperationRule,
            LKCustomerContractOperationService operationService
    ) {
        LocalDateTime withdrawnFrom = LocalDateTime.of(LocalDate.now(), operationService.getFromTime())
                .minusMinutes(contractOperationRule.getCheckInBeforeMinutes());
        LocalDateTime withdrawnTo = LocalDateTime.of(LocalDate.now(), operationService.getToTime())
                .minusMinutes(contractOperationRule.getCheckOutBeforeMinutes());

        if (
                (LocalDateTime.now().isAfter(withdrawnFrom) || LocalDateTime.now().isEqual(withdrawnFrom))
                        && LocalDateTime.now().isBefore(withdrawnTo)
        ) return AttendStatus.CHECK_OUT_WITHDRAWN;

        return AttendStatus.CHECK_OUT_IN_TIME;
    }
}
