package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.dto.ContractIdsRequest;
import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;
import com.eden.eden_crm_sec_crm_back.dto.external.CheckInData;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressRequest;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(value = "${feign.clients.attendance}", path = "/crm-sec-attendance")
public interface AttendanceFeignClient {
    @GetMapping(value = "/triggers")
    List<TriggerResponse> getAllTriggers();

    @GetMapping(value = "/triggers/{triggerId}")
    TriggerResponse getTrigger(@RequestParam Long triggerId);

    @PostMapping(value = "/v1/customer/reporting/attendances/contract-ids")
    Set<Long> getContractIdsForCheckedInWorkforcesToday(@RequestBody ContractIdsRequest contractIdsRequest);

    @GetMapping(value = "/external/workforce/check-in-data")
    CheckInData checkInData();

    @GetMapping(value = "/external/push-token/{workforceId}")
    String getPushToken(@PathVariable("workforceId") String workforceId);

    /**
     * Asks Attendance: is any workforce currently checked-in for this
     * (contract, site, serviceTime) at {@code asOfInstant}? Used by patrol and
     * assignment edit flows to choose between "cutoff = today" and "cutoff =
     * tomorrow".
     */
    @PostMapping(value = "/external/attendance/period-in-progress")
    PeriodInProgressResponse isPeriodInProgress(@RequestBody PeriodInProgressRequest request);
}
