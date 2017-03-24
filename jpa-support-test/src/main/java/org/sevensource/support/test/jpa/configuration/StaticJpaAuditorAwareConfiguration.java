package org.sevensource.support.test.jpa.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
public class StaticJpaAuditorAwareConfiguration {

	public final static String AUDITOR_STRING = "Jim Black";
	
	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> { return AUDITOR_STRING; };
	}
}