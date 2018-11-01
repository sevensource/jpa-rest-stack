package org.sevensource.support.test.jpa.configuration;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class StaticJpaAuditorAwareConfiguration {

	public static final String AUDITOR_STRING = "Jim Black";

	@Bean
	@Primary
	public AuditorAware<String> auditorAware() {
		return () -> Optional.of(AUDITOR_STRING);
	}
}