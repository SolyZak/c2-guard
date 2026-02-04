package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.clients.dto.WorkforceFullDataDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskPatrolExecutionRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskRepository;
import com.eden.eden_crm_sec_crm_back.service.WorkforceService;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.ExecuteDistributedTaskRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request.TodayTasksRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskExecutionSlotEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTasksResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.mappers.TaskDistributionMapper;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.TodayTaskSlotProjection;
import com.eden.eden_crm_sec_crm_back.taskdistribution.services.base.DistributedTaskService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DistributedTaskServiceImpl implements DistributedTaskService {
    private final WorkforceService workforceService;
    private final CustomerRepository customerRepository;
    private final TaskExecutionSlotRepository taskExecutionSlotRepository;
    private final TaskRepository taskRepository;
    private final TaskPatrolExecutionRepository taskPatrolExecutionRepository;
    private final TaskDistributionMapper taskDistributionMapper;

    @Override
    @Transactional
    public TodayTasksResponse getTodayTasks(TodayTasksRequest todayTasksRequest) {
        WorkforceFullDataDto workforceFullDataDto = workforceService.getLoggedInWorkforce();
        Customer customer = getCustomer(workforceFullDataDto.securityCompany().id());
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime todayMidnight = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime tomorrowMidnight = todayMidnight.plusDays(1);

        List<TodayTaskSlotProjection> slots = taskExecutionSlotRepository.findTodayTasks(
            customer.getId(),
            todayTasksRequest.contractId(),
            todayMidnight,
            tomorrowMidnight,
            todayTasksRequest.serviceId(),
            todayTasksRequest.serviceTimeId(),
            todayTasksRequest.slotNumber(),
            workforceFullDataDto.workforce().id()
        );

        List<TodayTaskEntryResponse> tasks = new ArrayList<>();
        Map<Long, List<TodayTaskSlotProjection>> groupedSlots = slots.stream()
                .sorted(Comparator.comparing(TodayTaskSlotProjection::getStartDateTime))
                .collect(Collectors.groupingBy(TodayTaskSlotProjection::getTaskDistributionId));

        groupedSlots.forEach((key, slotsList) -> {
            TodayTaskSlotProjection lastSlot = slotsList.getLast();
            List<TodayTaskExecutionSlotEntryResponse> slotResponses = taskDistributionMapper.toExecutionSlotResponseList(slotsList);
            TodayTaskEntryResponse todayTaskEntryResponse = taskDistributionMapper.toTodayTaskEntryResponse(lastSlot, slotResponses);
            tasks.add(todayTaskEntryResponse);
        });
        return TodayTasksResponse.builder().tasks(tasks).build();
    }

    @Override
    @Transactional
    public void executeTask(ExecuteDistributedTaskRequest executeDistributedTaskRequest) {
        WorkforceFullDataDto workforceFullDataDto = workforceService.getLoggedInWorkforce();
        Customer customer = getCustomer(workforceFullDataDto.securityCompany().id());
        TaskExecutionSlot taskExecutionSlot = getTaskExecutionSlot(executeDistributedTaskRequest.executionSlotId());
        Task task = taskExecutionSlot.getTaskDistribution().getTask();
        checkTaskExecutionConstraints(executeDistributedTaskRequest, taskExecutionSlot, task);
        TaskPatrolExecution taskPatrolExecution = createTaskPatrolExecution(executeDistributedTaskRequest, task, customer);
        taskPatrolExecution = taskPatrolExecutionRepository.save(taskPatrolExecution);
        taskExecutionSlot.setStatus(TaskDistributionStatus.FINISHED);
        taskExecutionSlot.setTaskExecution(taskPatrolExecution);
        taskExecutionSlot.setExecutedByWorkforceId(workforceFullDataDto.workforce().id());
    }

    private static void checkTaskExecutionConstraints(
        ExecuteDistributedTaskRequest executeDistributedTaskRequest,
        TaskExecutionSlot taskExecutionSlot,
        Task task
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        if (
            taskExecutionSlot.getStatus() != TaskDistributionStatus.CURRENT
                || now.isBefore(taskExecutionSlot.getStartDateTime())
                || now.isAfter(taskExecutionSlot.getEndDateTime())
        )
            throw new BusinessException(MessageUtil.getMessage("task.execute.error"), HttpStatus.BAD_REQUEST);

        if (task.getTaskChecks().size() != executeDistributedTaskRequest.checks().size())
            throw new BusinessException("Task check size not matched", HttpStatus.BAD_REQUEST);

        IntStream.range(0, task.getTaskChecks().size())
            .forEach(i -> {
                var expected = executeDistributedTaskRequest.checks().get(i).getClass();
                var actual = task.getTaskChecks().get(i).mapToResponse().getClass();

                if (!actual.equals(expected))
                    throw new BusinessException("Task check type not matched", HttpStatus.BAD_REQUEST);
            });
    }

    private static TaskPatrolExecution createTaskPatrolExecution(
        ExecuteDistributedTaskRequest executeDistributedTaskRequest,
        Task task,
        Customer customer
    ) {
        TaskPatrolExecution taskPatrolExecution = new TaskPatrolExecution();
        taskPatrolExecution.setId(task.getId());
        taskPatrolExecution.setName(task.getName());
        List<TaskCheckPatrolExecution> taskChecksPatrolExecution = createTaskCheckPatrolExecutions(executeDistributedTaskRequest, taskPatrolExecution);
        taskPatrolExecution.setTaskCheckPatrolExecutions(taskChecksPatrolExecution);
        taskPatrolExecution.setCustomer(customer);
        return taskPatrolExecution;
    }

    private static List<TaskCheckPatrolExecution> createTaskCheckPatrolExecutions(
        ExecuteDistributedTaskRequest executeDistributedTaskRequest,
        TaskPatrolExecution taskPatrolExecution
    ) {
        List<TaskCheckPatrolExecution> taskChecksPatrolExecution = new ArrayList<>(executeDistributedTaskRequest.checks().size());
        executeDistributedTaskRequest.checks()
            .forEach(check -> taskChecksPatrolExecution.add(check.mapToExecutionEntity(taskPatrolExecution)));
        return taskChecksPatrolExecution;
    }

    private TaskExecutionSlot getTaskExecutionSlot(Long executionSlotId) {
        return taskExecutionSlotRepository
            .findById(executionSlotId)
            .orElseThrow(() -> new BusinessException("Task execution slot not found", HttpStatus.NOT_FOUND));
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(UserNotProvided::new);
    }
}
