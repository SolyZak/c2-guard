package com.eden.eden_crm_sec_crm_back.locks.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Body returned in the {@code details} field of a 423 LOCKED_BY_OTHER_USER
 * error so the FE can render "locked by Mona until 14:30" without an extra
 * status call.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockConflictDetails {
    private String holderName;
    private OffsetDateTime expiresAt;
}
