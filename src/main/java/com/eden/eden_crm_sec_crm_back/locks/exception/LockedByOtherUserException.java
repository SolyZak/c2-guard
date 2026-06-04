package com.eden.eden_crm_sec_crm_back.locks.exception;

import com.eden.eden_crm_sec_crm_back.locks.dtos.LockConflictDetails;
import lombok.Getter;

/**
 * Thrown when a caller tries to act on a lock-guarded endpoint but the
 * customer's edit-lock is currently held by a different user. The {@code
 * details} payload (holder name, expiresAt) lets the FE render
 * "locked by Mona until 14:30" without an extra status call.
 */
@Getter
public class LockedByOtherUserException extends RuntimeException {
    private final LockConflictDetails details;

    public LockedByOtherUserException(LockConflictDetails details) {
        super("Resource locked by " + details.getHolderName());
        this.details = details;
    }
}
