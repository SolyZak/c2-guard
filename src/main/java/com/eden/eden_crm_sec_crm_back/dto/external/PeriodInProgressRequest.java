package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Mirror of {@code com.eden.eden_crm_attendance_back.dto.external.PeriodInProgressRequest}.
 * Sent over Feign to the attendance service to find out whether at least one
 * workforce is currently checked-in for the given (contract, site, serviceTime)
 * at {@code asOfInstant}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodInProgressRequest {
    private Long customerId;
    private Long contractId;
    private Long siteId;
    private Long serviceId;
    private Long serviceTimeId;
    private OffsetDateTime asOfInstant;
}
