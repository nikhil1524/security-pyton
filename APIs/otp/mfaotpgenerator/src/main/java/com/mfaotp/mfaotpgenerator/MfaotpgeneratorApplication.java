package com.mfaotp.mfaotpgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.mfaotp.mfaotpgenerator.repo")
@EntityScan("com.mfaotp.mfaotpgenerator.entities")
@SpringBootApplication(scanBasePackages = {"com.mfaotp.mfaotpgenerator"},exclude = {SecurityAutoConfiguration.class})
public class MfaotpgeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MfaotpgeneratorApplication.class, args);
	}

}
