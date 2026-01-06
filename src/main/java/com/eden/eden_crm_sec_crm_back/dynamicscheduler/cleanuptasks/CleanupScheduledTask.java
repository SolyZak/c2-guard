package com.eden.eden_crm_sec_crm_back.dynamicscheduler.cleanuptasks;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CleanupScheduledTask {

    private final ScheduledTaskRepository scheduledTaskRepository;

    @Scheduled(cron = "0 0 0 1 * ?") // Every first day of the month at midnight
    @Transactional
    public void cleanup() {
        log.info("Cleanup scheduled tasks started");
        long count = scheduledTaskRepository.countByIsExecutionFinishedTrue();
        if (count > 0) {
            scheduledTaskRepository.deleteByIsExecutionFinishedTrue();
        }
        log.info("Deleted [{}] finished scheduled tasks", count);
    }
}
