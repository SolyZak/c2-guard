package com.eden.eden_crm_sec_crm_back.dynamicscheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Arrays;

@Configuration
@Slf4j
public class SchedulerConfig {

    @Value("${dynamicscheduler.pool-size:50}")
    private int poolSize;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix("[DynamicScheduler]-");
        // instruct underlying executor to remove cancelled tasks from the queue if supported
        try {
            // remove on cancel policy exists on the underlying ScheduledThreadPoolExecutor
            scheduler.setRemoveOnCancelPolicy(true);
        } catch (NoSuchMethodError | Exception _e) {
            log.debug("removeOnCancelPolicy not available on this platform/version, skipping");
        }
        scheduler.setErrorHandler(t -> {
            log.error("Task error: {}", t.getMessage());
            log.error("StackTrace:\n {}", Arrays.stream(t.getStackTrace()).map(Object::toString).toList());
        });
        scheduler.initialize();
        return scheduler;
    }
}
