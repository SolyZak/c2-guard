package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.dto.request.task.*;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.enums.PatrolFrequencyEnum;
import com.eden.eden_crm_sec_crm_back.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.projections.TodayTasksProjection;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskPatrolExecutionRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskRepository;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.utils.DateUtils;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskPatrolExecutionRepository taskPatrolExecutionRepository;
    private final CustomerRepository customerRepository;
    private final ContractOperationSiteDistributionPatrolRepository repository;
    private final Utils utils;
    private final WorkforceService workforceService;
    @Override
    public PaginateResponse<TaskCheckDto> listTasks(Integer page, Integer size) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Task> taskPage = taskRepository.listTasks(pageable, customer.getId());
        List<TaskCheckDto> taskCheckDtos = new ArrayList<>();
        if (taskPage.hasContent()) {
            for (Task task : taskPage.getContent()) {
                TaskCheckDto dto = null;
                if (task.getTaskChecks() != null && task.getTaskChecks().size() > 0) {
                    List<TaskCheckDTO> taskCheckDTOS = new ArrayList<>();
                    for (TaskCheck taskCheck : task.getTaskChecks())
                        taskCheckDTOS.add(taskCheck.mapToResponse());
                    dto = new TaskCheckDto(task.getName(), taskCheckDTOS);
                } else {
                    dto = new TaskCheckDto(task.getName(), null);
                }
                taskCheckDtos.add(dto);
            }
        }

        return new PaginateResponse<>(
                taskCheckDtos,
                page,
                size,
                taskPage.getTotalElements(),
                (long) taskPage.getTotalPages()
        );
    }

    @Override
    public List<TaskDto> listTasksNoPaginationForLoggedInCustomer() {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        List<Task> tasks = taskRepository.listTasks(customer.getId());
        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : tasks) {
            TaskDto taskDto = new TaskDto(task.getName(), task.getId());
            taskDtos.add(taskDto);
        }
        return taskDtos;
    }

    @Override
    public List<TaskDto> listTasksNoPaginationForLoggedInCustomerByLocationIdAndPatrolId(Long locationId, Long patrolId) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        List<Task> tasks = taskRepository.listLoggedInTasksByPatrolIdAndLocationId(customer.getId(), patrolId, locationId);
        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : tasks) {
            TaskDto taskDto = new TaskDto(task.getName(), task.getId());
            taskDtos.add(taskDto);
        }
        return taskDtos;
    }

    @Override
    public TodayTasksResponseDto getTodayTasks(Long contractId, Long serviceId, Long siteId, String uniqueId) {
        WorkforceFullDataDto workforceFullDataDto = workforceService.getLoggedInWorkforce();
        Customer customer = customerRepository.findById(workforceFullDataDto.securityCompany().id()).orElseThrow(UserNotProvided::new);
        List<TodayTasksProjection> todayTasksProjections = taskRepository.getTodayTasksByServiceIdAndContractId(
                customer.getId(), contractId, serviceId, siteId, LocalDate.now(), uniqueId
                );
        Map<TodayTasks, List<TodayTasks>> map = new HashMap<>();
        for (TodayTasksProjection projection : todayTasksProjections) {
            TodayTasks search = new TodayTasks(
                    projection.getTaskName(),
                    projection.getPatrolId(),
                    projection.getPatrolName(),
                    projection.getLocationId(),
                    projection.getLocationAccessType(),
                    projection.getLocationName(),
                    projection.getPremiseId(),
                    projection.getPremiseName(),
                    projection.getStartDate(),
                    projection.getEndDate(),
                    projection.getStartTime(),
                    projection.getEndTime(),
                    projection.getPatrolFreqType(),
                    projection.getTaskId(),
                    projection.getPatrolDistributionId(),
                    projection.getPeriodStatus()
            );
            if (map.get(search) == null) {
                List<TodayTasks> list = new ArrayList<>();
                list.add(new TodayTasks(
                        projection.getTaskName(),
                        projection.getPatrolId(),
                        projection.getPatrolName(),
                        projection.getLocationId(),
                        projection.getLocationAccessType(),
                        projection.getLocationName(),
                        projection.getPremiseId(),
                        projection.getPremiseName(),
                        projection.getStartDate(),
                        projection.getEndDate(),
                        projection.getStartTime(),
                        projection.getEndTime(),
                        projection.getPatrolFreqType(),
                        projection.getTaskId(),
                        projection.getPatrolDistributionId(),
                        projection.getPeriodStatus()
                ));
                map.put(search, list);
            } else {
                map.get(search).add(new TodayTasks(
                        projection.getTaskName(),
                        projection.getPatrolId(),
                        projection.getPatrolName(),
                        projection.getLocationId(),
                        projection.getLocationAccessType(),
                        projection.getLocationName(),
                        projection.getPremiseId(),
                        projection.getPremiseName(),
                        projection.getStartDate(),
                        projection.getEndDate(),
                        projection.getStartTime(),
                        projection.getEndTime(),
                        projection.getPatrolFreqType(),
                        projection.getTaskId(),
                        projection.getPatrolDistributionId(),
                        projection.getPeriodStatus()
                ));
            }
        }
        List<TodayTaskEntryDto> tasks = new ArrayList<>();
        List<Long> missedIds = new ArrayList<>();
        for (Map.Entry<TodayTasks, List<TodayTasks>> entry : map.entrySet()) {
            List<TodayTaskEntryTimesDto> times = entry.getValue().stream()
                    .map(tt ->
                            new TodayTaskEntryTimesDto(
                                    DateUtils.toLocalTime(customer.getTimezone(), tt.getStartTime()),
                                    DateUtils.toLocalTime(customer.getTimezone(), tt.getEndTime()),
                                    getTimePeriodStatus(tt),
                                    tt.getPatrolDistributionId()
                            ))
                    .sorted(
                            Comparator.comparing(TodayTaskEntryTimesDto::getStartTime)
                    )
                    .toList();
            TodayTaskEntryDto task = new TodayTaskEntryDto(
                    entry.getKey().getTaskName(),
                    entry.getKey().getPatrolName(),
                    entry.getKey().getLocationName(),
                    entry.getKey().getPremiseName(),
                    entry.getKey().getPatrolFreqType(),
                    entry.getKey().getEndDate(),
                    times,
                    entry.getKey().getTaskId(),
                    entry.getKey().getPatrolId(),
                    entry.getKey().getPremiseId(),
                    entry.getKey().getLocationId(),
                    entry.getKey().getLocationAccessType()
            );
            tasks.add(task);
            missedIds.addAll(times.stream().filter(t -> t.getStatus().equals(TaskDistributionStatus.MISSED.name())).map(t -> t.getPatrolDistributionId()).collect(Collectors.toList()));
        }
        if (missedIds.size() > 0) {
            List<ContractOperationSiteDistributionPatrol> distributionPatrols = repository.findAllById(missedIds);
            for (int i = 0; i < distributionPatrols.size() ; i++) {
                distributionPatrols.get(i).setStatus(TaskDistributionStatus.MISSED.name());
            }
            repository.saveAll(distributionPatrols);
        }
        return new TodayTasksResponseDto(tasks);
    }

    private String getTimePeriodStatus(TodayTasks todayTasks) {
        if (todayTasks.getPeriodStatus().equals(TaskDistributionStatus.FINISHED.name()) || todayTasks.getPeriodStatus().equals(TaskDistributionStatus.MISSED.name())) {
            return todayTasks.getPeriodStatus();
        }
        if (todayTasks.getPatrolFreqType().equals(PatrolFrequencyEnum.EVERY_PERIOD.getFreq())) {
            LocalTime current = LocalTime.now();
            if (current.isBefore(todayTasks.getEndTime().toLocalTime()) && current.isAfter(todayTasks.getStartTime().toLocalTime())) {
                return TaskDistributionStatus.CURRENT.name();
            } else if (current.isAfter(todayTasks.getEndTime().toLocalTime()) && todayTasks.getPeriodStatus().equals(TaskDistributionStatus.CREATED.name())) {
                return TaskDistributionStatus.MISSED.name();
            }
            return TaskDistributionStatus.CREATED.name();
        } else {
            LocalDate currentDate = LocalDate.now();
            LocalTime current = LocalTime.now();
            if (currentDate.equals(todayTasks.getEndDate()) && current.isAfter(todayTasks.getEndTime().toLocalTime())) {
                return TaskDistributionStatus.MISSED.name();
            } else if (
                    ( currentDate.equals(todayTasks.getEndDate()) || currentDate.equals(todayTasks.getStartDate()) ) ||
                            ( currentDate.isBefore(todayTasks.getEndDate()) && currentDate.isAfter(todayTasks.getStartDate()) )
                            && current.isAfter(todayTasks.getStartTime().toLocalTime()) && current.isBefore(todayTasks.getEndTime().toLocalTime())
            ) {
                return TaskDistributionStatus.CURRENT.name();
            }
            return TaskDistributionStatus.CREATED.name();
        }
    }

    @Override
    public TaskCheckDto getTaskById(Long taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (!optionalTask.isPresent()){
            throw new BusinessException("not-found", HttpStatus.NOT_FOUND);
        }
        Task task = optionalTask.get();
        TaskCheckDto dto = null;
        if (task.getTaskChecks() != null && task.getTaskChecks().size() > 0) {
            List<TaskCheckDTO> taskCheckDTOS = new ArrayList<>();
            for (TaskCheck taskCheck : task.getTaskChecks())
                taskCheckDTOS.add(taskCheck.mapToResponse());
            dto = new TaskCheckDto(task.getName(), taskCheckDTOS);
        } else {
            dto = new TaskCheckDto(task.getName(), null);
        }
        return dto;
    }

    @Override
    @Transactional
    public void executeTask(AddTaskDistributionRequest request) {
        Optional<ContractOperationSiteDistributionPatrol> optionalDistribution =  repository.findById(request.getPatrolDistributionId());
        if (!optionalDistribution.isPresent()) {
            throw new BusinessException("not-found", HttpStatus.NOT_FOUND);
        }
        if (!optionalDistribution.get().getStatus().equals(TaskDistributionStatus.CREATED.name())) {
            throw new BusinessException("Can't execute task", HttpStatus.BAD_REQUEST);
        }
        Optional<Task> optionalTask = taskRepository.findById(request.getTaskId());
        if (!optionalTask.isPresent()) {
            throw new BusinessException("not-found", HttpStatus.NOT_FOUND);
        }
        ContractOperationSiteDistributionPatrol patrolDistribution = optionalDistribution.get();
        Task task = optionalTask.get();

        if (patrolDistribution.getPatrolFrequencyType().equals(PatrolFrequencyEnum.EVERY_PERIOD.getFreq()) && !LocalDate.now().equals(optionalDistribution.get().getStartDate())) {
            throw new BusinessException("Too early to start task", HttpStatus.BAD_REQUEST);
        }

        if (!patrolDistribution.getTask().getId().equals(task.getId())) {
            throw new BusinessException("not-found", HttpStatus.NOT_FOUND);
        }

        if (task.getTaskChecks().size() != request.getChecks().size()) {
            throw new BusinessException("not-found", HttpStatus.NOT_FOUND);
        } else {
            for (int i=0;i<task.getTaskChecks().size();i++) {
                if (!task.getTaskChecks().get(i).mapToResponse().getClass().equals(request.getChecks().get(i).getClass()))
                    throw new BusinessException("not-found", HttpStatus.NOT_FOUND);
            }
        }

        WorkforceFullDataDto workforceFullDataDto = workforceService.getLoggedInWorkforce();

        Customer customer = customerRepository.findById(workforceFullDataDto.securityCompany().id()).orElseThrow(UserNotProvided::new);
        TaskPatrolExecution taskPatrolExecution = new TaskPatrolExecution();
        taskPatrolExecution.setId(request.getTaskId());
        taskPatrolExecution.setName(task.getName());
        if (request.getChecks() != null) {
            List<TaskCheckPatrolExecution> taskChecksPatrolExecution = new ArrayList<>(request.getChecks().size());
            for (TaskCheckDTO dto : request.getChecks()) {
                taskChecksPatrolExecution.add(dto.mapToExecutionEntity(taskPatrolExecution));
            }
            taskPatrolExecution.setTaskCheckPatrolExecutions(taskChecksPatrolExecution);
        }
        taskPatrolExecution.setCustomer(customer);
        taskPatrolExecutionRepository.save(taskPatrolExecution);
        patrolDistribution.setStatus(TaskDistributionStatus.FINISHED.name());
    }

    @Override
    @Transactional
    public void addTask(AddTaskRequest request) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId())
                .orElseThrow(UserNotProvided::new);

        Task task = new Task();
        task.setName(request.getTaskName());

        if (request.getChecks() != null) {
            List<TaskCheck> taskChecks = new ArrayList<>(request.getChecks().size());

            for (TaskCheckDTO dto : request.getChecks()) {
                taskChecks.add(dto.mapToEntity(task));
            }
            task.setTaskChecks(taskChecks);
        }
        task.setCustomer(customer);
        taskRepository.save(task);
    }
}
