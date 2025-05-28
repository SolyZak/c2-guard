package com.eden.eden_crm_sec_crm_back;

import com.eden.eden_crm_sec_crm_back.base.model.AuditTrail;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@EnableAsync
@SpringBootApplication
@EnableJpaAuditing
public class EdenCrmSecCrmBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdenCrmSecCrmBackApplication.class, args);
	}
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Africa/Cairo"));
	}
}
