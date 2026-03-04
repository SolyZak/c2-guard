package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.rest;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.CreateTaskLocationChecksImageRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskCheckImageResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.TaskLocationChecksImageResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.TaskLocationChecksImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/task-location-checks-images")
@RequiredArgsConstructor
public class TaskLocationChecksImageController {

    private final TaskLocationChecksImageService service;

    @PostMapping
    public ResponseEntity<TaskLocationChecksImageResponse> create(
            @Valid @RequestBody CreateTaskLocationChecksImageRequest request) {
        TaskLocationChecksImageResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/checks/{taskCheckDefinitionId}/locations/{locationId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<TaskCheckImageResponse> uploadTaskCheckImage(
            @PathVariable Long taskCheckDefinitionId,
            @PathVariable Long locationId,
            @RequestParam("image") MultipartFile image) {
        return ApiResponse.ok(service.uploadTaskCheckImage(taskCheckDefinitionId, locationId, image));
    }
}