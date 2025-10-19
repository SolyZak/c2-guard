package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.task.*;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.TaskRepository;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    @Override
    public PaginateResponse<TaskCheckDto> listTasks(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Task> taskPage = taskRepository.tasksPaginate(pageable);
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
    @Transactional
    public void addTask(AddTaskRequest request) {
        Task task = new Task();
        task.setName(request.getTaskName());
        if (request.getChecks() != null) {
            List<TaskCheck> taskChecks = new ArrayList<>(request.getChecks().size());
            for (TaskCheckDTO dto : request.getChecks()) {
                taskChecks.add(dto.mapToEntity(task));
            }
            task.setTaskChecks(taskChecks);
        }
        taskRepository.save(task);
    }
}
