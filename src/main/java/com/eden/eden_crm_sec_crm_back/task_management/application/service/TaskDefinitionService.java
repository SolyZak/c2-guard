package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskCheckDefinitionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskDefinitionRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskDefinitionSummaryResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.mapper.TaskMapper;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskCheckDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.model.TaskDefinition;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskCheckDefinitionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.repository.TaskDefinitionRepository;
import com.eden.eden_crm_sec_crm_back.task_management.domain.service.TaskDefinitionDomainService;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.CheckType;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.Severity;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskDefinitionService {

    private final TaskDefinitionRepository taskDefinitionRepository;
    private final TaskCheckDefinitionRepository taskCheckDefinitionRepository;
    private final TaskDefinitionDomainService taskDefinitionDomainService;
    private final TaskMapper taskMapper;
    private final Utils utils;

    @Transactional
    public void createTaskDefinition(CreateTaskDefinitionRequest request) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        TaskDefinition taskDefinition = TaskDefinition.create(
                request.getName(),
                Severity.fromString(request.getSeverity()),
                customerId);
        taskDefinition = taskDefinitionRepository.save(taskDefinition);

        if (request.getChecks() != null && !request.getChecks().isEmpty()) {
            List<TaskCheckDefinition> checks = new ArrayList<>();
            for (CreateTaskCheckDefinitionRequest checkReq : request.getChecks()) {
                taskDefinitionDomainService.validateCheckDefinition(checkReq.getCheckSettings(), checkReq.getName());
                TaskCheckDefinition check = TaskCheckDefinition.create(
                        taskDefinition.getId(),
                        checkReq.getName(),
                        Severity.fromString(checkReq.getSeverity()),
                        CheckType.fromString(checkReq.getCheckType()),
                        checkReq.getCheckSettings(),
                        checkReq.isHasEvidence(),
                        checkReq.isHasComment(),
                        customerId);
                checks.add(taskCheckDefinitionRepository.save(check));
            }
            for (TaskCheckDefinition check : checks) {
                taskDefinition.addCheck(check);
            }
        }
    }

    public List<TaskDefinitionResponse> listTaskDefinitions(int page, int size) {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        List<TaskDefinition> tasks = taskDefinitionRepository.findAllByCustomerId(customerId, page, size);
        return taskMapper.toTaskDefinitionResponseList(tasks);
    }

    public long countTaskDefinitions() {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        return taskDefinitionRepository.countByCustomerId(customerId);
    }

    public List<TaskDefinitionSummaryResponse> listAllTaskDefinitions() {
        Long customerId = utils.getLoggedInUser().getCustomerId();
        List<TaskDefinition> tasks = taskDefinitionRepository.findAllByCustomerId(customerId);
        return taskMapper.toTaskDefinitionSummaryResponseList(tasks);
    }

    public TaskDefinitionResponse getTaskDefinition(Long id) {
        TaskDefinition task = taskDefinitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("task-not-found", HttpStatus.NOT_FOUND));
        return taskMapper.toTaskDefinitionResponse(task);
    }
}
