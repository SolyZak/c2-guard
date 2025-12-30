package com.eden.eden_crm_sec_crm_back;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@EnableAsync
@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
@Slf4j
public class EdenCrmSecCrmBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdenCrmSecCrmBackApplication.class, args);
	}

    @PostConstruct
    public void init() {
        log.info("JVM TZ: {}", ZoneId.systemDefault());
        log.info("Now: {}", OffsetDateTime.now());
        log.info("Now in Africa/Cairo: {}", OffsetDateTime.now(ZoneId.of("Africa/Cairo")));
        log.info("Now in Asia/Riyadh: {}", OffsetDateTime.now(ZoneId.of("Asia/Riyadh")));
        log.info("Now in Asia/Dubai: {}", OffsetDateTime.now(ZoneId.of("Asia/Dubai")));
    }
}
