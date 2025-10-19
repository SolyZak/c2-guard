package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.task.AddTaskRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.TaskCheckDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    @PostMapping
    public ApiResponse createTask(@RequestBody AddTaskRequest taskRequest) {
        taskService.addTask(taskRequest);
        return ApiResponse.created();
    }

    @GetMapping
    public ApiResponse<PaginateResponse<TaskCheckDto>> listLoggedInTasks(@RequestParam(defaultValue = "0", name = "page") Integer page,
                                                                         @RequestParam(defaultValue = "10", name = "size") Integer size) {
        return ApiResponse.ok(taskService.listTasks(page, size));
    }
}
