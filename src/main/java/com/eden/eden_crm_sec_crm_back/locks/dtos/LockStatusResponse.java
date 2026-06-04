package com.eden.eden_crm_sec_crm_back.locks.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockStatusResponse {
    private boolean held;
    private Boolean byCaller;
    private String holderName;
    private OffsetDateTime acquiredAt;
    private OffsetDateTime expiresAt;
    private Long remainingSeconds;
    private Integer currentIteration;
    private Integer totalIterations;
}
