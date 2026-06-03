package com.eden.eden_crm_sec_crm_back.locks.services;

import com.eden.eden_crm_sec_crm_back.locks.dtos.AcquireLockResponse;
import com.eden.eden_crm_sec_crm_back.locks.dtos.LockStatusResponse;

/**
 * Manages the per-patrol edit lock that covers both US1 (edit patrol) and US2
 * (edit distributions under that patrol). Keyed by {@code patrolId}. Multiple
 * patrols can be locked in parallel by different users; only one user per
 * patrol at a time.
 */
public interface CustomerEditLockService {

    /** Acquires the lock or refreshes it (same holder). Throws 423 if held by another user. */
    AcquireLockResponse acquire(Long patrolId);

    /** Returns the current lock state; never throws on conflict (read-only). */
    LockStatusResponse status(Long patrolId);

    /** Idempotently releases the lock if held by the caller. */
    void release(Long patrolId);

    /** Throws 423 unless the caller holds the lock for this patrol or no lock is held. */
    void requireHeldOrAbsent(Long patrolId);

    /** Returns true iff the caller currently holds an unexpired lock for this patrol. */
    boolean isHeldByCaller(Long patrolId);

    /** Background reaper - removes expired rows. */
    int reapExpired();
}
