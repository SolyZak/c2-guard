package com.eden.eden_crm_sec_crm_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EdenCrmSecCrmBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdenCrmSecCrmBackApplication.class, args);
	}

}
