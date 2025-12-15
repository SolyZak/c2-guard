package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.PatrolReportRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolReportResponseDto;

import java.util.List;

public interface PatrolReportService {
    List<PatrolReportResponseDto> generatePatrolReport(PatrolReportRequest patrolReportRequest);
}
