package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.external.*;
import com.eden.eden_crm_sec_crm_back.dto.response.WorkforceSiteDistributionDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExternalService {
    OperationSiteInfo getOperationSiteDetails(Long id);
    List<OperationSiteData> getCustomerOperationSites(Long customerId);
    CustomerInfo getCustomerInfo(Long id);
    WorkforceSiteDistributionDto operationSiteServicesDropdown(Long id, Long contractId);
    List<AttendanceStatsData> getAttendanceStats(AttendanceStatsDto dto);
    List<AttendanceWorkingPeriodData> getAttendanceDateWorkingPeriod(Long customerId, Long contractId, Long operationSiteId, LocalDate date);
    List<AttendanceWorkingPeriodData> getSecurityAttendanceDateWorkingPeriod(Long securityCompanyId, Long contractId, Long operationSiteId, LocalDate date);
    List<ContractPlannedQntDto> getContractPlannedQnt(Long customerId, Long securityCompanyId, List<Long> contractId, LocalDate from, LocalDate to);
    Map<String, Object> workforceHasActivity(WorkforceHasActivityRequest request);
    Map<Long, Long> getPremiseIdsByOperationSiteIds(Long customerId, List<Long> operationSiteIds);
    List<OperationSiteCameraData> getCamerasByOperationSiteId(Long operationSiteId);
}