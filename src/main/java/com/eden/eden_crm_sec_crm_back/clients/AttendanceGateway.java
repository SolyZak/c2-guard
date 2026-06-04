package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressRequest;
import com.eden.eden_crm_sec_crm_back.dto.external.PeriodInProgressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fail-closed wrapper around {@link AttendanceFeignClient#isPeriodInProgress}.
 * Any exception (network, 5xx, timeout) is treated as "inProgress=true" so
 * edits default to the safe path of deferring changes to the next period
 * rather than risking disruption of a workforce that might be on duty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceGateway {

    private final AttendanceFeignClient feignClient;

    public PeriodInProgressResponse isPeriodInProgress(PeriodInProgressRequest req) {
        try {
            PeriodInProgressResponse resp = feignClient.isPeriodInProgress(req);
            if (resp == null) {
                log.warn("Attendance returned null for period-in-progress; defaulting to inProgress=true");
                return failClosed();
            }
            return resp;
        } catch (Exception e) {
            log.warn("Attendance period-in-progress call failed; defaulting to inProgress=true: {}", e.getMessage());
            return failClosed();
        }
    }

    private PeriodInProgressResponse failClosed() {
        return PeriodInProgressResponse.builder()
                .inProgress(true)
                .checkedInWorkforceIds(List.of())
                .build();
    }
}
