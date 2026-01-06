package com.eden.eden_crm_sec_crm_back.dynamicscheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@Slf4j
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(50);
        scheduler.setThreadNamePrefix("[DynamicScheduler]-");
        scheduler.setErrorHandler(t -> log.error("Task error: {}", t.getMessage()));
        scheduler.initialize();
        return scheduler;
    }
}
