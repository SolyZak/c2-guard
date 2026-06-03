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
public class AcquireLockResponse {
    private OffsetDateTime acquiredAt;
    private OffsetDateTime expiresAt;
    private int ttlSeconds;
    private int iterationSeconds;
    private int totalIterations;
}
