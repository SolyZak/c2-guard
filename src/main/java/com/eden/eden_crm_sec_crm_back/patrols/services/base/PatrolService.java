package com.eden.eden_crm_sec_crm_back.patrols.services.base;

import com.eden.eden_crm_sec_crm_back.patrols.dtos.request.PatrolReportRequest;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.PatrolReportDetailsResponse;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.PatrolReportResponseDto;

import java.util.List;

public interface PatrolService {
    List<PatrolReportResponseDto> generatePatrolReport(PatrolReportRequest patrolReportRequest);
    PatrolReportDetailsResponse getPatrolReportDetails(Long premiseId, Long patrolId);
}
