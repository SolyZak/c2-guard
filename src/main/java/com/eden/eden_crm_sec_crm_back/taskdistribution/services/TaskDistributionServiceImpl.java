package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.PatrolDetailRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolTaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskAssignment;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskDistribution;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskAssignmentRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.TaskDistributionService;
import com.eden.eden_crm_sec_crm_back.utils.DateUtils;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TaskDistributionServiceImpl implements TaskDistributionService {
    public static final String DAILY_RATE = "daily";
    public static final String WEEKLY_RATE = "weekly";
    public static final String MONTHLY_RATE = "monthly";
    private final CustomerRepository customerRepository;
    private final CustomerContractRepository customerContractRepository;
    private final LKCustomerContractServiceRepository customerContractServiceRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final LKCustomerContractOperationServiceRepository customerContractOperationServiceRepository;
    private final PatrolDetailRepository patrolDetailRepository;
    private final TaskDistributionRepository taskDistributionRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final Utils utils;
    private record TaskTimeWindow(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {}

    @Override
    @Transactional
    public void distributePatrolTasks(DistributePatrolTaskRequest distributePatrolTaskRequest) {
        validatePatrolDistributionStartDate(distributePatrolTaskRequest.startDate());

        Customer customer = getLoggedInCustomer();
        CustomerContract contract = getContract(distributePatrolTaskRequest.contractId());
        LKCustomerContractService service = getService(distributePatrolTaskRequest.serviceId());
        SiteDistribution siteDistribution = getSiteDistribution(distributePatrolTaskRequest);
        LKCustomerContractOperationService serviceTime = getServiceTime(distributePatrolTaskRequest.serviceTimeId(), siteDistribution);
        List<PatrolDetail> patrolDetails = getPatrolDetails(distributePatrolTaskRequest.patrolDetailIds());

        OffsetDateTime assignedAt = OffsetDateTime.now();
        List<TaskAssignment> taskAssignments = createTaskAssignmentsByQuantity(customer, serviceTime.getQuantity().intValue(), assignedAt);
        Set<DayOfWeek> targetDays = extractTargetDays(serviceTime);

        Map<String, List<TaskTimeWindow>> timeWindowsCache = new HashMap<>();
        List<TaskDistribution> distributionsToSave = new ArrayList<>();

        patrolDetails.forEach(patrolDetail -> {
            Patrol patrol = validateAndGetPatrol(patrolDetail, customer);
            TaskDistribution taskDistribution = buildPatrolTaskDistributionBase(customer, contract, service, siteDistribution, patrolDetail);
            PatrolTaskDistribution patrolTaskDistribution = buildPatrolTaskDistribution(customer, taskDistribution, patrolDetail, serviceTime, patrol);
            taskDistribution.setPatrolTaskDistribution(patrolTaskDistribution);

            List<TaskTimeWindow> timeWindows = getOrBuildPatrolTimeWindows(
                    timeWindowsCache,
                    customer,
                    patrol,
                    distributePatrolTaskRequest.startDate(),
                    contract.getEndAgreementDate(),
                    serviceTime,
                    targetDays
            );

            List<TaskExecutionSlot> executionSlots = buildExecutionSlotsFromTimeWindows(
                    customer,
                    taskDistribution,
                    taskAssignments,
                    timeWindows
            );
            patrolTaskDistribution.setDistributedQuantity(executionSlots.size());
            taskDistribution.setDistributionTimes(executionSlots);

            distributionsToSave.add(taskDistribution);
        });

        taskDistributionRepository.saveAll(distributionsToSave);

        // add scheduled tasks
    }

    @Override
    public void distributeImmediateTasks(DistributeImmediateTaskRequest distributeImmediateTaskRequest) {

    }

    private static void validatePatrolDistributionStartDate(LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now()))
            throw new BusinessException(
                MessageUtil.getMessage("validation.contract.distribution.start-date.invalid"),
                HttpStatus.BAD_REQUEST
            );
    }

    private Customer getLoggedInCustomer() {
        return customerRepository
            .findById(utils.getLoggedInUser().getCustomerId())
            .orElseThrow(UserNotProvided::new);
    }

    private CustomerContract getContract(Long contractId) {
        return customerContractRepository
            .findById(contractId)
            .orElseThrow(() -> new BusinessException("validation.contract.invalid", HttpStatus.NOT_FOUND));
    }

    private LKCustomerContractService getService(Long serviceId) {
        return customerContractServiceRepository
            .findById(serviceId)
            .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("validation.service.invalid"), HttpStatus.NOT_FOUND));
    }

    private SiteDistribution getSiteDistribution(DistributePatrolTaskRequest distributePatrolTaskRequest) {
        return siteDistributionRepository
            .findBySiteIdAndContractIdAndServiceId(
                distributePatrolTaskRequest.siteId(),
                distributePatrolTaskRequest.contractId(),
                distributePatrolTaskRequest.serviceId()
            )
            .orElseThrow(() -> new BusinessException("invalid contract and service combination", HttpStatus.BAD_REQUEST));
    }

    private LKCustomerContractOperationService getServiceTime(Long serviceTimeId, SiteDistribution siteDistribution) {
        return customerContractOperationServiceRepository
            .findByIdAndSiteDistribution_Id(serviceTimeId, siteDistribution.getId())
            .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("validation.service.invalid"), HttpStatus.NOT_FOUND));
    }

    private List<PatrolDetail> getPatrolDetails(List<Long> patrolDetailIds) {
        List<PatrolDetail> patrolDetails = patrolDetailRepository.findAllById(patrolDetailIds);
        if (patrolDetails.size() != patrolDetailIds.size()) {
            throw new BusinessException(MessageUtil.getMessage("validation.patrol.invalid"), HttpStatus.NOT_FOUND);
        }
        return patrolDetails;
    }

    private List<TaskAssignment> createTaskAssignmentsByQuantity(Customer customer, int quantity, OffsetDateTime assignedAt) {
        return taskAssignmentRepository.saveAll(
            IntStream.rangeClosed(1, quantity)
                .mapToObj(i ->
                    TaskAssignment.builder()
                        .slotNumber(i)
                        .assignedAt(assignedAt)
                        .customer(customer)
                        .workforceId(null)
                        .build()
                )
                .toList()
        );
    }

    private TaskAssignment createTaskAssignmentByWorkforceId(Customer customer, Long workforceId, OffsetDateTime assignedAt) {
        return taskAssignmentRepository.save(
            TaskAssignment.builder()
                .slotNumber(null)
                .assignedAt(assignedAt)
                .customer(customer)
                .workforceId(workforceId)
                .build()
        );
    }

    private static TaskDistribution buildPatrolTaskDistributionBase(
        Customer customer,
        CustomerContract contract,
        LKCustomerContractService service,
        SiteDistribution siteDistribution,
        PatrolDetail patrolDetail
    ) {
        return TaskDistribution.builder()
            .contract(contract)
            .service(service)
            .site(siteDistribution.getSite())
            .location(patrolDetail.getLocation())
            .task(patrolDetail.getTask())
            .customer(customer)
            .distributionType(DistributionType.PATROL)
            .build();
    }

    private static PatrolTaskDistribution buildPatrolTaskDistribution(
        Customer customer,
        TaskDistribution taskDistribution,
        PatrolDetail patrolDetail,
        LKCustomerContractOperationService serviceTime,
        Patrol patrol
    ) {
        return PatrolTaskDistribution.builder()
            .taskDistribution(taskDistribution)
            .patrolDetailId(patrolDetail)
            .serviceTime(serviceTime)
            .frequencyRate(patrol.getFrequencyRate())
            .customer(customer)
            .build();
    }

    private static List<TaskTimeWindow> getOrBuildPatrolTimeWindows(
        Map<String, List<TaskTimeWindow>> timeWindowsCache,
        Customer customer,
        Patrol patrol,
        LocalDate startDate,
        LocalDate endDate,
        LKCustomerContractOperationService serviceTime,
        Set<DayOfWeek> targetDays
    ) {
        String scheduleKey = patrol.getFrequency() + "|" + patrol.getFrequencyRate();
        return timeWindowsCache.computeIfAbsent(
            scheduleKey,
            k -> buildPatrolTimeWindows(
                customer,
                patrol,
                startDate,
                endDate,
                serviceTime.getFromTime(),
                serviceTime.getToTime(),
                targetDays
            )
        );
    }

    private static List<TaskExecutionSlot> buildExecutionSlotsFromTimeWindows(
        Customer customer,
        TaskDistribution taskDistribution,
        List<TaskAssignment> taskAssignments,
        List<TaskTimeWindow> timeWindows
    ) {
        return timeWindows
            .stream()
            .flatMap(timeWindow ->
                taskAssignments
                    .stream()
                    .map(taskAssignment ->
                        TaskExecutionSlot.builder()
                            .taskDistribution(taskDistribution)
                            .taskAssignment(taskAssignment)
                            .startDateTime(timeWindow.startDateTime())
                            .endDateTime(timeWindow.endDateTime())
                            .status(TaskDistributionStatus.CREATED)
                            .customer(customer)
                            .build()
                    )
            )
            .toList();
    }

    private static List<TaskTimeWindow> buildPatrolTimeWindows(
        Customer customer,
        Patrol patrol,
        LocalDate startDate,
        LocalDate endDate,
        OffsetTime fromTime,
        OffsetTime toTime,
        Set<DayOfWeek> targetDays
    ) {
        return switch (patrol.getFrequency()) {
            case "once" -> buildOnceFrequencyTimeWindows(customer, patrol, startDate, endDate, fromTime, toTime, targetDays);
            case "every-period" -> buildEveryPeriodFrequencyTimeWindows(customer, patrol, startDate, endDate, fromTime, toTime, targetDays);
            default -> buildEveryPeriodFrequencyTimeWindows(customer, patrol, startDate, endDate, fromTime, toTime, targetDays);
        };
    }

    private static List<TaskTimeWindow> buildOnceFrequencyTimeWindows(
        Customer customer,
        Patrol patrol,
        LocalDate startDate,
        LocalDate endDate,
        OffsetTime fromTime,
        OffsetTime toTime,
        Set<DayOfWeek> targetDays
    ) {
        long count = getCountByFrequencyRateBetweenTwoDates(patrol.getFrequencyRate(), startDate, endDate);
        long safeCount = Math.max(0, count);

        return Stream
            .iterate(startDate, date -> incrementStartDateForOncePatrolType(patrol.getFrequencyRate(), date))
            .limit(safeCount)
            .filter(date -> targetDays.contains(date.getDayOfWeek()))
            .map(date -> {
                LocalDate currentEndDate = calculateEndDateForOncePatrolType(patrol.getFrequencyRate(), date);
                OffsetDateTime startDateTime = DateUtils.withTimeZone(customer.getTimezone(), date, fromTime);
                OffsetDateTime endDateTime = DateUtils.withTimeZone(customer.getTimezone(), currentEndDate, toTime);
                return new TaskTimeWindow(startDateTime, endDateTime);
            })
            .toList();
    }

    private static List<TaskTimeWindow> buildEveryPeriodFrequencyTimeWindows(
        Customer customer,
        Patrol patrol,
        LocalDate startDate,
        LocalDate endDate,
        OffsetTime fromTime,
        OffsetTime toTime,
        Set<DayOfWeek> targetDays
    ) {
        int frequencyRateMinutes = Integer.parseInt(patrol.getFrequencyRate());
        List<LocalDate> executionDates = getDatesByWeekDays(startDate, endDate, targetDays);
        List<OffsetTime> executionTimes = getTimeSteps(fromTime, toTime, frequencyRateMinutes);

        return executionDates
            .stream()
            .flatMap(date ->
                IntStream
                    .range(0, executionTimes.size())
                    .mapToObj(x -> {
                        OffsetTime slotStart = executionTimes.get(x);
                        OffsetTime slotEnd = x < executionTimes.size() - 1 ? executionTimes.get(x + 1) : toTime;
                        OffsetDateTime startDateTime = DateUtils.withTimeZone(customer.getTimezone(), date, slotStart);
                        OffsetDateTime endDateTime = DateUtils.withTimeZone(customer.getTimezone(), date, slotEnd);
                        return new TaskTimeWindow(startDateTime, endDateTime);
                    })
            )
            .toList();
    }

    private static Set<DayOfWeek> extractTargetDays(LKCustomerContractOperationService serviceTime) {
        return serviceTime.getDays()
            .stream()
            .map(d -> DayOfWeek.valueOf(d.getCode()))
            .collect(Collectors.toSet());
    }

    private static Patrol validateAndGetPatrol(PatrolDetail patrolDetail, Customer customer) {
        Patrol patrol = patrolDetail.getPatrol();
        if (
            patrol == null
                || patrol.getCustomer() == null
                || !Objects.equals(patrol.getCustomer().getId(), customer.getId())
        ) {
            throw new BusinessException(MessageUtil.getMessage("validation.patrol.invalid"), HttpStatus.NOT_FOUND);
        }
        return patrol;
    }

    private static LocalDate calculateEndDateForOncePatrolType(String frequencyRate, LocalDate startDate) {
        return switch (frequencyRate) {
            case DAILY_RATE -> startDate;
            case WEEKLY_RATE -> startDate.plusWeeks(1);
            case MONTHLY_RATE -> startDate.plusMonths(1);
            default -> throw new IllegalArgumentException("Invalid unit. Use 'days', 'weeks', or 'months'.");
        };
    }

    private static LocalDate incrementStartDateForOncePatrolType(String frequencyRate, LocalDate startDate) {
        return switch (frequencyRate) {
            case DAILY_RATE -> startDate.plusDays(1);
            case WEEKLY_RATE -> startDate.plusWeeks(1);
            case MONTHLY_RATE -> startDate.plusMonths(1);
            default -> throw new IllegalArgumentException("Invalid unit. Use 'days', 'weeks', or 'months'.");
        };
    }

    private static long getCountByFrequencyRateBetweenTwoDates(String frequencyRate, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || frequencyRate == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        return switch (frequencyRate) {
            case DAILY_RATE -> ChronoUnit.DAYS.between(startDate, endDate);
            case WEEKLY_RATE -> ChronoUnit.WEEKS.between(startDate, endDate);
            case MONTHLY_RATE -> ChronoUnit.MONTHS.between(startDate, endDate);
            default -> throw new IllegalArgumentException("Invalid unit. Use 'days', 'weeks', or 'months'.");
        };
    }

    private static List<LocalDate> getDatesByWeekDays(LocalDate startDate, LocalDate endDate, Set<DayOfWeek> targetDays) {
        return startDate
            .datesUntil(endDate.plusDays(1))
            .filter(date -> targetDays.contains(date.getDayOfWeek()))
            .toList();
    }

    private static List<OffsetTime> getTimeSteps(OffsetTime start, OffsetTime end, int minutesStep) {
        if (minutesStep <= 0)
            throw new IllegalArgumentException("Step must be greater than zero");

        return Stream
            .iterate(start, time -> time.isBefore(end), time -> time.plusMinutes(minutesStep))
            .toList();
    }
}
