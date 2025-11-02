package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolRequest;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.PatrolService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "/patrol")
@RequiredArgsConstructor
public class PatrolController {

    private final PatrolService patrolService;
    @PostMapping
    ApiResponse addPatrol(@RequestBody @Valid AddPatrolRequest request) throws IOException, WriterException {
        patrolService.addPatrol(request);
        return ApiResponse.created();
    }

    @GetMapping
    ApiResponse listPatrols(@RequestParam(defaultValue = "0", name = "page") Integer page,
                             @RequestParam(defaultValue = "10", name = "size") Integer size,
                             @RequestParam(required = false, name = "search") String search) throws IOException, WriterException {
        return ApiResponse.ok(patrolService.listPatrol(page, size, search));
    }

    @GetMapping("/all")
    ApiResponse listPatrolsNoPagination() throws IOException, WriterException {
        return ApiResponse.ok(patrolService.listAllPatrols());
    }
}
