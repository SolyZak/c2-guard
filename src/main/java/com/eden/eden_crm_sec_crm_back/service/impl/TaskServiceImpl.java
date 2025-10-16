package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.task.*;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.repository.TaskRepository;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
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
