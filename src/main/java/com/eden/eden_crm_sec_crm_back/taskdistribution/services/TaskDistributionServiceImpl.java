package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.repository.*;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.AvailableServiceTimesRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributeImmediateTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.DistributePatrolTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.AvailableServiceTimeResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.*;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.DistributionType;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskAssignmentRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskDistributionRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.CreateScheduledTaskForDistributionService;
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
import java.util.*;
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
    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerContractRepository customerContractRepository;
    private final LKCustomerContractServiceRepository customerContractServiceRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final LKCustomerContractOperationServiceRepository customerContractOperationServiceRepository;
    private final PatrolDetailRepository patrolDetailRepository;
    private final TaskRepository taskRepository;
    private final TaskDistributionRepository taskDistributionRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final LocationRepository locationRepository;
    private final AttendanceFeignClient attendanceClient;
    private final CreateScheduledTaskForDistributionService createScheduledTaskForDistributionService;
    private final Utils utils;
    private record TaskTimeWindow(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {}

    @Override
    @Transactional
    public void distributePatrolTasks(DistributePatrolTaskRequest distributePatrolTaskRequest) {
        validatePatrolDistributionStartDate(distributePatrolTaskRequest.startDate());

        UserData loggedInUser = getLoggedInUser();
        Customer customer = getLoggedInCustomer(loggedInUser.getCustomerId());
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

        for(PatrolDetail patrolDetail : patrolDetails) {
            Patrol patrol = validateAndGetPatrol(patrolDetail, customer);
            TaskDistribution taskDistribution = buildTaskDistributionBase(
                customer,
                contract,
                patrolDetail.getTask(),
                DistributionType.PATROL
            );
            PatrolTaskDistribution patrolTaskDistribution = buildPatrolTaskDistribution(customer, taskDistribution, service, patrolDetail, serviceTime, patrol);
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
            taskDistribution.setExecutionSlots(executionSlots);

            distributionsToSave.add(taskDistribution);
        }

        distributionsToSave = taskDistributionRepository.saveAllAndFlush(distributionsToSave);
        distributionsToSave.forEach(taskDistribution ->
            createScheduledTaskForDistributionService.createDistributionScheduledTasks(
                "patrolTaskDistributionId",
                taskDistribution.getPatrolTaskDistribution().getId(),
                taskDistribution.getExecutionSlots()
            )
        );
    }

    @Override
    @Transactional
    public void distributeImmediateTasks(DistributeImmediateTaskRequest distributeImmediateTaskRequest) {
        UserData loggedInUser = getLoggedInUser();
        Customer customer = getLoggedInCustomer(loggedInUser.getCustomerId());
        Set<Long> contractIds = attendanceClient.getContractIdsForCheckedInWorkforcesToday(distributeImmediateTaskRequest.workforceIds());
        if (contractIds.size() != 1)
            throw new BusinessException("Workforces must belong to the same contract", HttpStatus.BAD_REQUEST);

        CustomerContract contract = getContract(contractIds.iterator().next());
        Task task = getTask(distributeImmediateTaskRequest.taskId());
        TaskDistribution taskDistribution = buildTaskDistributionBase(
            customer,
            contract,
            task,
            DistributionType.IMMEDIATE
        );
        ImmediateTaskDistribution immediateTaskDistribution = buildImmediateTaskDistribution(
            customer,
            taskDistribution,
            Long.valueOf(loggedInUser.getId()),
            distributeImmediateTaskRequest
        );
        taskDistribution.setImmediateTaskDistribution(immediateTaskDistribution);

        OffsetDateTime assignedAt = OffsetDateTime.now();
        List<TaskAssignment> taskAssignments = createTaskAssignmentsByWorkforceId(customer, distributeImmediateTaskRequest.workforceIds(), assignedAt);
        List<TaskExecutionSlot> executionSlots = buildExecutionSlotsForImmediateTask(customer, taskDistribution, taskAssignments);
        taskDistribution.setExecutionSlots(executionSlots);
        taskDistribution = taskDistributionRepository.saveAndFlush(taskDistribution);

        createScheduledTaskForDistributionService.createDistributionScheduledTasks(
            "ImmediateTaskDistributionId",
            taskDistribution.getImmediateTaskDistribution().getId(),
            taskDistribution.getExecutionSlots()
        );
    }

    @Override
    public List<AvailableServiceTimeResponse> getAllAvailableServiceTimes(AvailableServiceTimesRequest availableServiceTimesRequest) {
        UserData loggedInUser = getLoggedInUser();
        Customer customer = getLoggedInCustomer(loggedInUser.getCustomerId());
        CustomTimezone customerTimezone = customer.getTimezone();
        List<LKCustomerContractOperationService> serviceTimes = customerContractOperationServiceRepository.findAvailableServiceTimes(
            availableServiceTimesRequest.contractId(),
            availableServiceTimesRequest.serviceId(),
            availableServiceTimesRequest.siteId(),
            availableServiceTimesRequest.patrolId()
        );

        List<AvailableServiceTimeResponse> result = new ArrayList<>();
        serviceTimes.forEach(serviceTime ->
            result.add(
                AvailableServiceTimeResponse.builder()
                    .serviceTimeId(serviceTime.getId())
                    .startTime(DateUtils.withTimeZone(customerTimezone, serviceTime.getFromTime()))
                    .endTime(DateUtils.withTimeZone(customerTimezone, serviceTime.getToTime()))
                    .build()
            )
        );
        return result;
    }

    private static void validatePatrolDistributionStartDate(LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now()))
            throw new BusinessException(
                MessageUtil.getMessage("validation.contract.distribution.start-date.invalid"),
                HttpStatus.BAD_REQUEST
            );
    }

    private Customer getLoggedInCustomer(Long customerId) {
        return customerRepository.findById(customerId)
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

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException("invalid task", HttpStatus.BAD_REQUEST));
    }

    private Optional<Location> getOptionalLocation(Long locationId) {
        return locationRepository.findById(locationId);
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

    private List<TaskAssignment> createTaskAssignmentsByWorkforceId(Customer customer, Set<Long> workforceIds, OffsetDateTime assignedAt) {
        return taskAssignmentRepository.saveAll(
            workforceIds.stream()
                .map(workforceId ->
                    TaskAssignment.builder()
                        .slotNumber(null)
                        .assignedAt(assignedAt)
                        .customer(customer)
                        .workforceId(workforceId)
                        .build()
                )
                .toList()
        );
    }

    private static TaskDistribution buildTaskDistributionBase(
        Customer customer,
        CustomerContract contract,
        Task task,
        DistributionType distributionType
    ) {
        return TaskDistribution.builder()
            .contract(contract)
            .task(task)
            .customer(customer)
            .distributionType(distributionType)
            .build();
    }

    private static PatrolTaskDistribution buildPatrolTaskDistribution(
        Customer customer,
        TaskDistribution taskDistribution,
        LKCustomerContractService service,
        PatrolDetail patrolDetail,
        LKCustomerContractOperationService serviceTime,
        Patrol patrol
    ) {
        return PatrolTaskDistribution.builder()
            .taskDistribution(taskDistribution)
            .patrolDetail(patrolDetail)
            .service(service)
            .location(patrolDetail.getLocation())
            .serviceTime(serviceTime)
            .frequencyRate(patrol.getFrequencyRate())
            .customer(customer)
            .build();
    }

    private ImmediateTaskDistribution buildImmediateTaskDistribution(
        Customer customer,
        TaskDistribution taskDistribution,
        Long loggedInUserId,
        DistributeImmediateTaskRequest distributeImmediateTaskRequest
    ) {
        CustomerUser user = new CustomerUser();
        user.setId(loggedInUserId);
        Optional<Location> location = getOptionalLocation(distributeImmediateTaskRequest.locationId());

        return ImmediateTaskDistribution.builder()
            .taskDistribution(taskDistribution)
            .location(location.orElse(null))
            .locationName(distributeImmediateTaskRequest.locationName())
            .latitude(distributeImmediateTaskRequest.latitude())
            .longitude(distributeImmediateTaskRequest.longitude())
            .customer(customer)
            .dispatcher(user)
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

    private static List<TaskExecutionSlot> buildExecutionSlotsForImmediateTask(
        Customer customer,
        TaskDistribution taskDistribution,
        List<TaskAssignment> taskAssignments
    ) {
        return taskAssignments
            .stream()
            .map(taskAssignment ->
                TaskExecutionSlot.builder()
                    .taskDistribution(taskDistribution)
                    .taskAssignment(taskAssignment)
                    .status(TaskDistributionStatus.CREATED)
                    .customer(customer)
                    .build()
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
                LocalDate actualEndDate = toTime.isBefore(fromTime) ? currentEndDate.plusDays(1) : currentEndDate;
                OffsetDateTime startDateTime = DateUtils.withTimeZone(customer.getTimezone(), date, fromTime);
                OffsetDateTime endDateTime = DateUtils.withTimeZone(customer.getTimezone(), actualEndDate, toTime);
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
                        LocalDate actualEndDate = slotEnd.isBefore(slotStart) ? date.plusDays(1) : date;
                        OffsetDateTime startDateTime = DateUtils.withTimeZone(customer.getTimezone(), date, slotStart);
                        OffsetDateTime endDateTime = DateUtils.withTimeZone(customer.getTimezone(), actualEndDate, slotEnd);
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
        validateStepSize(minutesStep);

        if (representsFullDayShift(start, end))
            return generateFullDayTimeSteps(start, minutesStep);

        if (isOvernightShift(start, end))
            return generateOvernightTimeSteps(start, end, minutesStep);

        return generateSameDayTimeSteps(start, end, minutesStep);
    }

    private static void validateStepSize(int minutesStep) {
        if (minutesStep <= 0)
            throw new RuntimeException("Step must be greater than zero");
    }

    private static boolean representsFullDayShift(OffsetTime start, OffsetTime end) {
        return start.equals(end);
    }

    private static boolean isOvernightShift(OffsetTime start, OffsetTime end) {
        return !start.isBefore(end);
    }

    private static List<OffsetTime> generateFullDayTimeSteps(OffsetTime start, int minutesStep) {
        List<OffsetTime> fullDaySteps = new ArrayList<>();
        OffsetTime currentTime = start;

        int totalSteps = calculateStepsForFullDay(minutesStep);
        for (int i = 0; i < totalSteps; i++) {
            fullDaySteps.add(currentTime);
            currentTime = currentTime.plusMinutes(minutesStep);
        }

        return fullDaySteps;
    }

    private static int calculateStepsForFullDay(int minutesStep) {
        return 1440 / minutesStep; // 1440 minutes in 24 hours
    }

    private static List<OffsetTime> generateOvernightTimeSteps(OffsetTime start, OffsetTime end, int minutesStep) {
        OffsetTime midnight = createMidnightWithSameOffset(start);

        List<OffsetTime> beforeMidnight = generateTimeStepsUntilMidnight(start, midnight, minutesStep);
        OffsetTime continuationPoint = calculateContinuationPointAfterMidnight(beforeMidnight, minutesStep);
        List<OffsetTime> afterMidnight = generateTimeStepsFromContinuationToEnd(continuationPoint, end, minutesStep);

        return combineTimeStepLists(beforeMidnight, afterMidnight);
    }

    private static OffsetTime createMidnightWithSameOffset(OffsetTime referenceTime) {
        return OffsetTime.of(0, 0, 0, 0, referenceTime.getOffset());
    }

    private static List<OffsetTime> generateTimeStepsUntilMidnight(OffsetTime start, OffsetTime midnight, int minutesStep) {
        List<OffsetTime> steps = new ArrayList<>();
        OffsetTime currentTime = start;
        OffsetTime nextTime;

        while (true) {
            steps.add(currentTime);
            nextTime = currentTime.plusMinutes(minutesStep);

            if (currentTime.equals(midnight) || hasWrappedAroundToNextDay(currentTime, nextTime))
                break;

            currentTime = nextTime;
        }

        return steps;
    }

    private static boolean hasWrappedAroundToNextDay(OffsetTime currentTime, OffsetTime nextTime) {
        int currentHour = currentTime.getHour();
        int nextHour = nextTime.getHour();
        return nextHour < currentHour;
    }

    private static OffsetTime calculateContinuationPointAfterMidnight(List<OffsetTime> beforeMidnight, int minutesStep) {
        OffsetTime lastTimeBeforeMidnight = beforeMidnight.getLast();
        return lastTimeBeforeMidnight.plusMinutes(minutesStep);
    }

    private static List<OffsetTime> generateTimeStepsFromContinuationToEnd(OffsetTime continuationPoint, OffsetTime end, int minutesStep) {
        if (continuationPoint == null || !continuationPoint.isBefore(end))
            return new ArrayList<>();

        return Stream
            .iterate(continuationPoint, time -> time.isBefore(end), time -> time.plusMinutes(minutesStep))
            .toList();
    }

    private static List<OffsetTime> generateSameDayTimeSteps(OffsetTime start, OffsetTime end, int minutesStep) {
        return Stream
            .iterate(start, time -> time.isBefore(end), time -> time.plusMinutes(minutesStep))
            .toList();
    }

    private static List<OffsetTime> combineTimeStepLists(List<OffsetTime> firstList, List<OffsetTime> secondList) {
        List<OffsetTime> combined = new ArrayList<>(firstList);
        combined.addAll(secondList);
        return combined;
    }

    private UserData getLoggedInUser() {
        return utils.getLoggedInUser();
    }
}
