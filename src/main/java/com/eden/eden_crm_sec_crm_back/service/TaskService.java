package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.task.AddTaskRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskDto;
import com.eden.eden_crm_sec_crm_back.dto.response.TodayTasksResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;

public interface TaskService {

    void addTask(AddTaskRequest request);
    PaginateResponse<TaskCheckDto> listTasks(Integer page, Integer size);
    List<TaskDto> listTasksNoPaginationForLoggedInCustomer();

    List<TaskDto> listTasksNoPaginationForLoggedInCustomerByLocationIdAndPatrolId(Long locationId, Long patrolId);

    TodayTasksResponseDto getTodayTasks(Long contractId, Long serviceId, Long siteId, String uniqueId);
}
