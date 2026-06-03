package com.eden.eden_crm_sec_crm_back.locks.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Lock timing knobs. {@code ttlSeconds} is the only thing the backend enforces;
 * {@code iterationSeconds} is published to the FE so it can render iteration
 * banners (e.g. "remaining 2 iterations"). {@code totalIterations} is derived.
 */
@Configuration
@ConfigurationProperties(prefix = "eden.locks")
@Getter
@Setter
public class CustomerEditLockProperties {

    /** Lock lifetime in seconds. Default 1800 = 30 minutes. */
    private int ttlSeconds = 1800;

    /** Iteration cadence in seconds. Default 600 = 10 minutes. */
    private int iterationSeconds = 600;

    public int totalIterations() {
        if (iterationSeconds <= 0) return 1;
        return Math.max(1, ttlSeconds / iterationSeconds);
    }
}
