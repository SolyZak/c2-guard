package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.ContractDistributionForPatrol;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationsTasksForPatrol;
import com.eden.eden_crm_sec_crm_back.enums.PatrolFrequencyEnum;
import com.eden.eden_crm_sec_crm_back.enums.PatrolFrequencyRateEnum;
import com.eden.eden_crm_sec_crm_back.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import com.eden.eden_crm_sec_crm_back.repository.*;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.ContractOperationSiteDistributionPatrolService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractOperationSiteDistributionPatrolServiceImpl implements ContractOperationSiteDistributionPatrolService {
    private final ContractOperationSiteDistributionPatrolRepository repository;
    private final PatrolRepository patrolRepository;
    private final CustomerSiteRepository customerSiteRepository;
    private final LocationRepository locationRepository;
    private final TaskRepository taskRepository;
    private final CustomerRepository customerRepository;
    private final CustomerContractRepository contractRepository;
    private final LKCustomerContractServiceRepository contractServiceRepository;
    private final Utils utils;
    private final SiteDistributionRepository siteDistributionRepository;
    @Override
    @Transactional
    public void add(List<ContractDistributionForPatrol> requestList, Long contractId, Long serviceId) {
        Optional<CustomerContract> optionalCustomerContract =  contractRepository.findById(contractId);
        if (!optionalCustomerContract.isPresent()) {
            throw new BusinessException("validation.contract.invalid", HttpStatus.NOT_FOUND);
        }

        Optional<LKCustomerContractService> optionalCustomerService = contractServiceRepository.findById(serviceId);
        if (!optionalCustomerService.isPresent()) {
            throw new BusinessException(MessageUtil.getMessage("validation.service.invalid"), HttpStatus.NOT_FOUND);
        }


        if (requestList != null && requestList.size() > 0) {
            Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
            for (ContractDistributionForPatrol request : requestList) {
                if (request.getStartDate().isBefore(LocalDate.now())) {
                    throw new BusinessException(MessageUtil.getMessage("validation.contract.distribution.start-date.invalid"), HttpStatus.BAD_REQUEST);
                }
                Optional<Patrol> patrolOptional = patrolRepository.findById(request.getPatrolId());
                if (!patrolOptional.isPresent()) {
                    throw new BusinessException(MessageUtil.getMessage("validation.patrol.invalid"), HttpStatus.NOT_FOUND);
                }
                Optional<CustomerSite> customerSiteOptional = customerSiteRepository.findById(request.getSiteId());
                if (!customerSiteOptional.isPresent()) {
                    throw new BusinessException(MessageUtil.getMessage("validation.customer-site.invalid"), HttpStatus.NOT_FOUND);
                }
                Map<Long, List<Long>> locations = new HashMap<>();
                if (request.getLocations() != null) {
                    Set<Long> taskIds = new HashSet<>();
                    Set<Long> locationIds = new HashSet<>();
                    for (LocationsTasksForPatrol locationTasksForPatrol : request.getLocations()) {
                        locationIds.add(locationTasksForPatrol.getLocationId());
                        taskIds.addAll(locationTasksForPatrol.getTasks());
                        locations.put(locationTasksForPatrol.getLocationId(), locationTasksForPatrol.getTasks());
                    }
                    List<Task> tasks = taskRepository.listTasksByIds(customer.getId(), taskIds);
                    if (tasks.size() != taskIds.size()) {
                        throw new BusinessException(MessageUtil.getMessage("validation.task.invalid"), HttpStatus.NOT_FOUND);
                    }
                    List<LocationProjection> locationProjections = locationRepository.listAllLoggedInCustomerLocationsByIds(customer.getId(), locationIds);
                    if (locationProjections.size() != locationIds.size()) {
                        throw new BusinessException(MessageUtil.getMessage("validation.location.invalid"), HttpStatus.NOT_FOUND);
                    }
                }
                if (patrolOptional.get().getFrequency().equals(PatrolFrequencyEnum.ONCE.getFreq())) {
                    handlePatrolOnceFrequency(patrolOptional, request, optionalCustomerContract, locations,
                            optionalCustomerService, contractId, serviceId);
                } else {
                    handlePatrolEveryPeriodFrequency(patrolOptional, request, optionalCustomerContract, locations,
                            optionalCustomerService, contractId, serviceId);
                }
            }
        }
    }

    private void handlePatrolEveryPeriodFrequency(Optional<Patrol> patrolOptional, ContractDistributionForPatrol request,
                                                  Optional<CustomerContract> optionalCustomerContract,
                                                  Map<Long, List<Long>> locations,
                                                  Optional<LKCustomerContractService> optionalCustomerService,
                                                  Long contractId, Long serviceId
    ) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        Optional<SiteDistribution> sd = siteDistributionRepository.findOneByContractAndLKCustomerServiceAndSiteId(contractId, serviceId, request.getSiteId());
        if (!sd.isPresent()) {
            throw new BusinessException("invalid contract and service combination", HttpStatus.BAD_REQUEST);
        }
        List<ContractOperationSiteDistributionPatrol> distributionForPatrols = new ArrayList<>();
        LKCustomerContractOperationService details = sd.get().getOperationServices().stream().
                filter(os -> os.getId().equals(
                        Long.parseLong(request.getTimePeriodId().split("_")[0])
                        )
                ).findFirst().get();
        ContractOperationSiteDistributionPatrol contractDistributionForPatrol = null;

        Set<String> weekDays = details.getDays().stream().map(dayEnum -> dayEnum.getCode()).collect(Collectors.toSet());

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = optionalCustomerContract.get().getEndAgreementDate();

        List<LocalDate> executionDates = getDatesByWeekDays(startDate, endDate, weekDays.stream().map(wd-> DayOfWeek.valueOf(wd)).collect(Collectors.toSet()));
        int frequencyRate = Integer.parseInt(patrolOptional.get().getFrequencyRate());
        List<OffsetTime> executionTimes = getTimeSteps(details.getFromTime(), details.getToTime(), frequencyRate);

        for (Map.Entry<Long, List<Long>> entry : locations.entrySet()) {
            for (Long tId : entry.getValue()) {
                for (LocalDate date : executionDates) {
                    for (int x = 0; x < executionTimes.size(); x++) {
                            contractDistributionForPatrol = new ContractOperationSiteDistributionPatrol(
                                    null,
                                    request.getPatrolId(),
                                    request.getSiteId(),
                                    date,
                                    date,
                                    entry.getKey(),
                                    tId,
                                    optionalCustomerService.get(),
                                    optionalCustomerContract.get(),
                                    executionTimes.get(x),
                                    x < executionTimes.size() - 1 ? executionTimes.get(x + 1) : details.getToTime(),
                                    customer,
                                    patrolOptional.get().getFrequency(),
                                    TaskDistributionStatus.CREATED.name(),
                                    request.getTimePeriodId()
                            );
                            distributionForPatrols.add(contractDistributionForPatrol);
                    }
                }
            }
        }
        repository.saveAll(distributionForPatrols);
    }

    private void handlePatrolOnceFrequency(Optional<Patrol> patrolOptional, ContractDistributionForPatrol request,
                                           Optional<CustomerContract> optionalCustomerContract,
                                           Map<Long, List<Long>> locations,
                                           Optional<LKCustomerContractService> optionalCustomerService,
                                           Long contractId, Long serviceId
    ) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        Optional<SiteDistribution> sd = siteDistributionRepository.findOneByContractAndLKCustomerServiceAndSiteId(contractId, serviceId, request.getSiteId());
        if (!sd.isPresent()) {
            throw new BusinessException("invalid contract and service combination", HttpStatus.BAD_REQUEST);
        }
        LKCustomerContractOperationService details = sd.get().getOperationServices().stream().
                filter(os -> os.getId().equals(
                        Long.parseLong(request.getTimePeriodId().split("_")[0])
                        )
                ).findFirst().get();

        ContractOperationSiteDistributionPatrol contractDistributionForPatrol = new ContractOperationSiteDistributionPatrol();
        contractDistributionForPatrol.setStartDate(request.getStartDate());

        Set<String> weekDays = details.getDays().stream().map(dayEnum -> dayEnum.getCode()).collect(Collectors.toSet());
        long count = getCountByFrequencyRateBetweenTwoDates(patrolOptional.get().getFrequencyRate(), request.getStartDate(), optionalCustomerContract.get().getEndAgreementDate());
        List<ContractOperationSiteDistributionPatrol> distributionForPatrols = new ArrayList<>();

        for (Map.Entry<Long, List<Long>> entry : locations.entrySet()) {
            for (Long tId : entry.getValue()) {
                for (long i = 0; i < count; i++) {

                        contractDistributionForPatrol = new ContractOperationSiteDistributionPatrol(
                                null,
                                request.getPatrolId(),
                                request.getSiteId(),
                                contractDistributionForPatrol.getStartDate(),
                                calculateEndDateForOncePatrolType(patrolOptional.get().getFrequencyRate(), contractDistributionForPatrol.getStartDate()),
                                entry.getKey(),
                                tId,
                                optionalCustomerService.get(),
                                optionalCustomerContract.get(),
                                details.getFromTime(),
                                details.getToTime(),
                                customer,
                                patrolOptional.get().getFrequency(),
                                TaskDistributionStatus.CREATED.name(),
                                request.getTimePeriodId()
                        );
                        if (i == 0) {
                            if (weekDays.contains(contractDistributionForPatrol.getStartDate().getDayOfWeek().name().toUpperCase()))
                                distributionForPatrols.add(contractDistributionForPatrol);
                            continue;
                        }

                        if (patrolOptional.get().getFrequencyRate().equals(PatrolFrequencyRateEnum.DAILY.getRate())) {
                            contractDistributionForPatrol.setStartDate(contractDistributionForPatrol.getStartDate().plusDays(1));
                        } else if (patrolOptional.get().getFrequencyRate().equals(PatrolFrequencyRateEnum.WEEKLY.getRate())) {
                            contractDistributionForPatrol.setStartDate(contractDistributionForPatrol.getStartDate().plusWeeks(1));
                        } else if (patrolOptional.get().getFrequencyRate().equals(PatrolFrequencyRateEnum.MONTHLY.getRate())) {
                            contractDistributionForPatrol.setStartDate(contractDistributionForPatrol.getStartDate().plusMonths(1));
                        } else {
                            throw new IllegalArgumentException("Invalid unit. Use 'days', 'weeks', or 'months'.");
                        }

                        if (weekDays.contains(contractDistributionForPatrol.getStartDate().getDayOfWeek().name().toUpperCase()))
                            distributionForPatrols.add(contractDistributionForPatrol);
                }
            }
        }
            repository.saveAll(distributionForPatrols);
    }

    private LocalDate calculateEndDateForOncePatrolType(String frequencyRate, LocalDate startDate) {
        if (frequencyRate.equals(PatrolFrequencyRateEnum.DAILY.getRate())) {
            return startDate.plusDays(1);
        } else if (frequencyRate.equals(PatrolFrequencyRateEnum.WEEKLY.getRate())) {
            return startDate.plusWeeks(1);
        } else if (frequencyRate.equals(PatrolFrequencyRateEnum.MONTHLY.getRate())) {
            return startDate.plusMonths(1);
        } else {
            throw new IllegalArgumentException("Invalid unit. Use 'days', 'weeks', or 'months'.");
        }
    }

    long getCountByFrequencyRateBetweenTwoDates(String frequencyRate, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || frequencyRate == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        if (frequencyRate.equals(PatrolFrequencyRateEnum.DAILY.getRate())) {
            return ChronoUnit.DAYS.between(startDate, endDate);
        } else if (frequencyRate.equals(PatrolFrequencyRateEnum.WEEKLY.getRate())) {
            return ChronoUnit.WEEKS.between(startDate, endDate);
        } else if (frequencyRate.equals(PatrolFrequencyRateEnum.MONTHLY.getRate())) {
            return ChronoUnit.MONTHS.between(startDate, endDate);
        } else {
            throw new IllegalArgumentException("Invalid unit. Use 'days', 'weeks', or 'months'.");
        }
    }

    static List<LocalDate> getDatesByWeekDays(LocalDate startDate, LocalDate endDate, Set<DayOfWeek> targetDays) {
        List<LocalDate> result = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (targetDays.contains(current.getDayOfWeek())) {
                result.add(current);
            }
            current = current.plusDays(1);
        }

        return result;
    }

    static List<OffsetTime> getTimeSteps(OffsetTime start, OffsetTime end, int minutesStep) {
        List<OffsetTime> result = new ArrayList<>();

        if (minutesStep <= 0) {
            throw new IllegalArgumentException("Step must be greater than zero");
        }

        OffsetTime current = start;
        while (current.isBefore(end)) {
            result.add(current);
            current = current.plusMinutes(minutesStep);
        }

        return result;
    }
}
