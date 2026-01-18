package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.OrgUnitClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.dto.GeneralDropdown;
import com.eden.eden_crm_sec_crm_back.dto.request.WorkforceLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionWorkingPeriodDto;
import com.eden.eden_crm_sec_crm_back.enums.AttendStatus;
import com.eden.eden_crm_sec_crm_back.enums.PresenceMode;
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
import com.eden.eden_crm_sec_crm_back.utils.DateUtils;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
                    .name(site.getName() + " - " + site.getPremise().getName())
                    .contractId(contractId)
                    .contractName(contract.getAgreementName())
                    .customerId(contract.getCustomer().getId())
                    .customerName(contract.getCustomer().getName())
                    .latitude(site.getLatitude())
                    .longitude(site.getLongitude())
                    .tolerance(site.getTolerance())
                    .timezone(site.getTimezone())
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
                                        .activities(d.getActivities())
                                        .periods(
                                                d.getOperationServices().stream()
                                                        .map(os -> WorkforceSiteDistributionWorkingPeriodDto.builder()
                                                                .id(os.getId())
                                                                .fromTime(DateUtils.toLocalTime(site.getTimezone(), getFromTime(contractOperationRule, os)))
                                                                .toTime(DateUtils.toLocalTime(site.getTimezone(), getToTime(os)))
                                                                .isWorking(isWorkingPeriod(contractOperationRule, os))
                                                                .checkInStatus(checkInStatus(contractOperationRule, os))
                                                                .checkOutStatus(checkOutStatus(contractOperationRule, os))
                                                                .presenceMode(getPresenceMode(contractOperationRule))
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
//        return customerSiteRepository.findAll().stream().map(customerSiteMapper::toDropdown).toList();
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        return customerSiteRepository.findBySecurityCompanyId(workforceFullDataDto.securityCompany().id(), LocalDate.now())
                .stream().map(customerSiteMapper::toDropdown).toList();
    }

    @Override
    public WorkforceSiteDistributionDto operationSiteServicesDropdown(Long id, Long contractId) {
        WorkforceFullDataDto workforceFullDataDto = getLoggedInWorkforce();
        List<SiteDistribution> distributions = siteDistributionRepository.listForSecurityCompanyActiveTodayAndSiteId(
                workforceFullDataDto.securityCompany().id(), id, contractId, LocalDate.now()
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
                .timezone(site.getTimezone())
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
                                    .activities(d.getActivities())
                                    .periods(
                                            d.getOperationServices().stream()
                                                    .filter(os -> os.getDays().contains(weekDaysEnum))
                                                    .flatMap(os ->
                                                            LongStream.range(0, os.getQuantity())  // repeat for quantity times
                                                                    .mapToObj(i -> WorkforceSiteDistributionWorkingPeriodDto.builder()
                                                                            .id(os.getId())
                                                                            .patrolPeriodId(os.getId() + "_" + i)
                                                                            .fromTime(DateUtils.toLocalTime(site.getTimezone(), getFromTime(contractOperationRule, os)))
                                                                            .toTime(DateUtils.toLocalTime(site.getTimezone(), getToTime(os)))
                                                                            .isWorking(isWorkingPeriod(contractOperationRule, os))
                                                                            .checkInStatus(checkInStatus(contractOperationRule, os))
                                                                            .checkOutStatus(checkOutStatus(contractOperationRule, os))
                                                                            .presenceMode(getPresenceMode(contractOperationRule))
                                                                            .build()
                                                                    )
                                                    )
                                                    .toList()
                                    )
                                    .build();
                        }).toList()
                )
                .build();
    }

    public WorkforceFullDataDto getLoggedInWorkforce() {
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

    @Override
    public void addWorkforceLocation(Long id, WorkforceLocationRequest workforceLocationRequest) {
        orgUnitClient.addWorkforceLocation(id, workforceLocationRequest);
    }

    private boolean isWorkingPeriod(
            ContractOperationRule rule,
            LKCustomerContractOperationService service
    ) {
        OffsetDateTime now = DateUtils.nowDateTime(service.getSiteDistribution().getSite().getTimezone());

        OffsetTime fromTime = getFromTime(rule, service);
        OffsetDateTime from = now.with(fromTime);

        // Build "to" datetime
        OffsetTime toTime = service.getToTime();
        OffsetDateTime to = now.with(toTime);

        // Handle "from" possibly on previous day
        if (to.isBefore(from)) {
            // The period crosses midnight
            if (now.isBefore(to)) {
                // It's after midnight but before "to" => "from" was yesterday
                from = from.minusDays(1);
            } else {
                // It's after "to" but before midnight => "to" is tomorrow
                to = to.plusDays(1);
            }
        }

        return now.isAfter(from) && now.isBefore(to);
    }

    private AttendStatus checkInStatus(
            ContractOperationRule rule,
            LKCustomerContractOperationService service
    ) {
        OffsetTime now = OffsetDateTime.now(ZoneOffset.UTC).toOffsetTime();

        OffsetTime fromTime = service.getFromTime();

        int checkInBefore = getSafe(rule != null ? rule.getCheckInBeforeMinutes() : null);
        int checkInAfter = getSafe(rule != null ? rule.getCheckInAfterMinutes() : null);

        OffsetTime earlyFrom = fromTime.minusMinutes(checkInBefore);

        if (!now.isBefore(earlyFrom) && !now.isAfter(fromTime)) {
            return AttendStatus.CHECK_IN_EARLY;
        }

        OffsetTime inTimeTo = fromTime.plusMinutes(checkInAfter);

        if (!now.isBefore(fromTime) && !now.isAfter(inTimeTo)) {
            return AttendStatus.CHECK_IN_IN_TIME;
        }

        return AttendStatus.CHECK_IN_LATE;
    }

    private AttendStatus checkOutStatus(
            ContractOperationRule rule,
            LKCustomerContractOperationService service
    ) {
        OffsetTime now = OffsetDateTime.now(ZoneOffset.UTC).toOffsetTime();

        OffsetTime fromTime = service.getFromTime();
        OffsetTime toTime = service.getToTime();

        int checkInBefore = getSafe(rule != null ? rule.getCheckInBeforeMinutes() : null);
        int checkOutBefore = getSafe(rule != null ? rule.getCheckOutBeforeMinutes() : null);

        OffsetTime withdrawnFrom = fromTime.minusMinutes(checkInBefore);
        OffsetTime withdrawnTo = toTime.minusMinutes(checkOutBefore);

        if (!now.isBefore(withdrawnFrom) && now.isBefore(withdrawnTo)) {
            return AttendStatus.CHECK_OUT_WITHDRAWN;
        }

        return AttendStatus.CHECK_OUT_IN_TIME;
    }

    private PresenceMode getPresenceMode(ContractOperationRule rule) {
        return rule != null && rule.getPresenceMode() != null
                ? rule.getPresenceMode()
                : PresenceMode.BOTH;
    }

    private OffsetTime getFromTime(ContractOperationRule rule, LKCustomerContractOperationService service) {
        int checkInBefore = getSafe(rule != null ? rule.getCheckInBeforeMinutes() : null);
        return service.getFromTime().minusMinutes(checkInBefore);
    }

    private OffsetTime getToTime(LKCustomerContractOperationService service) {
        return service.getToTime();
    }

    private int getSafe(Integer minutes) {
        return minutes != null ? minutes : 0;
    }
}
