package com.eden.eden_crm_sec_crm_back.locks.tasks;

import com.eden.eden_crm_sec_crm_back.locks.services.CustomerEditLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Defensive reaper - deletes expired customer_edit_lock rows so the table never
 * grows unbounded. The acquire query already overrides expired rows, so this is
 * cleanup, not correctness.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEditLockReaperJob {

    private final CustomerEditLockService lockService;

    @Scheduled(fixedDelayString = "PT30S")
    public void reap() {
        int removed = lockService.reapExpired();
        if (removed > 0) {
            log.debug("Reaped {} expired customer_edit_lock rows", removed);
        }
    }
}
