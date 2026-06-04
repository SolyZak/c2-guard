package com.eden.eden_crm_sec_crm_back.locks.services.impl;

import com.eden.eden_crm_sec_crm_back.locks.config.CustomerEditLockProperties;
import com.eden.eden_crm_sec_crm_back.locks.dtos.AcquireLockResponse;
import com.eden.eden_crm_sec_crm_back.locks.dtos.LockConflictDetails;
import com.eden.eden_crm_sec_crm_back.locks.dtos.LockStatusResponse;
import com.eden.eden_crm_sec_crm_back.locks.entities.CustomerEditLock;
import com.eden.eden_crm_sec_crm_back.locks.exception.LockedByOtherUserException;
import com.eden.eden_crm_sec_crm_back.locks.repositories.CustomerEditLockRepository;
import com.eden.eden_crm_sec_crm_back.locks.services.CustomerEditLockService;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerEditLockServiceImpl implements CustomerEditLockService {

    private final CustomerEditLockRepository repository;
    private final CustomerEditLockProperties properties;
    private final Utils utils;

    @Override
    @Transactional
    public AcquireLockResponse acquire(Long patrolId) {
        UserData caller = utils.getLoggedInUser();
        Long userId = Long.valueOf(caller.getId());

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusSeconds(properties.getTtlSeconds());

        int updated = repository.upsertIfFreeOrSameHolder(
                patrolId, userId, caller.getName(), now, expiresAt);

        if (updated == 0) {
            CustomerEditLock existing = repository.findByPatrolId(patrolId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Lock conflict reported but no row found for patrol " + patrolId));
            throw new LockedByOtherUserException(LockConflictDetails.builder()
                    .holderName(existing.getHolderUserName())
                    .expiresAt(existing.getExpiresAt())
                    .build());
        }

        return AcquireLockResponse.builder()
                .acquiredAt(now)
                .expiresAt(expiresAt)
                .ttlSeconds(properties.getTtlSeconds())
                .iterationSeconds(properties.getIterationSeconds())
                .totalIterations(properties.totalIterations())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LockStatusResponse status(Long patrolId) {
        UserData caller = utils.getLoggedInUser();
        Optional<CustomerEditLock> opt = repository.findByPatrolId(patrolId);
        OffsetDateTime now = OffsetDateTime.now();

        if (opt.isEmpty() || opt.get().getExpiresAt().isBefore(now)) {
            return LockStatusResponse.builder().held(false).build();
        }

        CustomerEditLock lock = opt.get();
        long remaining = Duration.between(now, lock.getExpiresAt()).getSeconds();
        long elapsed = Duration.between(lock.getAcquiredAt(), now).getSeconds();
        int currentIteration = (int) Math.min(
                properties.totalIterations(),
                (elapsed / Math.max(1, properties.getIterationSeconds())) + 1
        );

        boolean byCaller = lock.getHolderUserId().equals(Long.valueOf(caller.getId()));
        return LockStatusResponse.builder()
                .held(true)
                .byCaller(byCaller)
                .holderName(lock.getHolderUserName())
                .acquiredAt(lock.getAcquiredAt())
                .expiresAt(lock.getExpiresAt())
                .remainingSeconds(remaining)
                .currentIteration(currentIteration)
                .totalIterations(properties.totalIterations())
                .build();
    }

    @Override
    @Transactional
    public void release(Long patrolId) {
        UserData caller = utils.getLoggedInUser();
        repository.releaseByHolder(patrolId, Long.valueOf(caller.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public void requireHeldOrAbsent(Long patrolId) {
        Optional<CustomerEditLock> opt = repository.findByPatrolId(patrolId);
        if (opt.isEmpty()) return;
        CustomerEditLock lock = opt.get();
        OffsetDateTime now = OffsetDateTime.now();
        if (lock.getExpiresAt().isBefore(now)) return;

        UserData caller = utils.getLoggedInUser();
        if (lock.getHolderUserId().equals(Long.valueOf(caller.getId()))) return;

        throw new LockedByOtherUserException(LockConflictDetails.builder()
                .holderName(lock.getHolderUserName())
                .expiresAt(lock.getExpiresAt())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHeldByCaller(Long patrolId) {
        UserData caller = utils.getLoggedInUser();
        Optional<CustomerEditLock> opt = repository.findByPatrolId(patrolId);
        if (opt.isEmpty()) return false;
        CustomerEditLock lock = opt.get();
        return lock.getExpiresAt().isAfter(OffsetDateTime.now())
                && lock.getHolderUserId().equals(Long.valueOf(caller.getId()));
    }

    @Override
    @Transactional
    public int reapExpired() {
        return repository.deleteExpired(OffsetDateTime.now());
    }
}
