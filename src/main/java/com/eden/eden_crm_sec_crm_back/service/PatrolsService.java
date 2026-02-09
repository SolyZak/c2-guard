package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolKeyValueDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;


public interface PatrolsService {
    void addPatrol(AddPatrolRequest request);
    PaginateResponse<PatrolResponseDto> listPatrol(Integer page, Integer size, String search);
    List<PatrolKeyValueDto> listAllPatrols();
}
