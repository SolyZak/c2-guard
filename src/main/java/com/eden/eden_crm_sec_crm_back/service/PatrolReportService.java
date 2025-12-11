package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.response.PatrolReportResponseDto;

public interface PatrolReportService {
    PatrolReportResponseDto generatePatrolReport(Long securityCompanyId, Long contractId);
}

