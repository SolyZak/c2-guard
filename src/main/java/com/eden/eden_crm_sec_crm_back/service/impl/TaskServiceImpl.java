package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.task.*;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.projections.TodayTasksProjection;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskRepository;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final CustomerRepository customerRepository;
    private final Utils utils;
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
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        List<TodayTasksProjection> todayTasksProjections = taskRepository.getTodayTasksByServiceIdAndContractId(
                customer.getId(), contractId, serviceId, siteId, LocalDate.now(), uniqueId
                );
        Map<TodayTasks, List<TodayTasks>> map = new HashMap<>();
        for (TodayTasksProjection projection : todayTasksProjections) {
            TodayTasks search = new TodayTasks(
                    projection.getTaskName(),
                    projection.getPatrolName(),
                    projection.getLocationName(),
                    projection.getPremiseName(),
                    projection.getEndDate(),
                    projection.getPatrolFreqType()
            );
            if (map.get(search) == null) {
                List<TodayTasks> list = new ArrayList<>();
                list.add(new TodayTasks(
                        projection.getTaskName(),
                        projection.getPatrolName(),
                        projection.getLocationName(),
                        projection.getPremiseName(),
                        projection.getEndDate(),
                        projection.getStartTime(),
                        projection.getEndTime(),
                        projection.getPatrolFreqType()
                ));
                map.put(search, list);
            } else {
                map.get(search).add(new TodayTasks(
                        projection.getTaskName(),
                        projection.getPatrolName(),
                        projection.getLocationName(),
                        projection.getPremiseName(),
                        projection.getEndDate(),
                        projection.getStartTime(),
                        projection.getEndTime(),
                        projection.getPatrolFreqType()
                ));
            }
        }
        List<TodayTaskEntryDto> tasks = new ArrayList<>();
        for (Map.Entry<TodayTasks, List<TodayTasks>> entry : map.entrySet()) {
            List<TodayTaskEntryTimesDto> times = entry.getValue().stream()
                    .map(tt -> new TodayTaskEntryTimesDto(tt.getStartTime(),tt.getEndTime())).sorted(Comparator.comparing(TodayTaskEntryTimesDto::getStartTime)).collect(Collectors.toList());
            TodayTaskEntryDto task = new TodayTaskEntryDto(
                    entry.getKey().getTaskName(),
                    entry.getKey().getPatrolName(),
                    entry.getKey().getLocationName(),
                    entry.getKey().getPremiseName(),
                    entry.getKey().getPatrolFreqType(),
                    entry.getKey().getEndDate(),
                    times
            );
            tasks.add(task);
        }
        return new TodayTasksResponseDto(tasks);
    }

    @Override
    @Transactional
    public void addTask(AddTaskRequest request) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
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
