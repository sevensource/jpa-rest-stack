package org.sevensource.support.jpa.configuration;

import java.util.Optional;

import org.sevensource.support.jpa.domain.SimpleEntity;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackageClasses=SimpleEntity.class)
@AutoConfigurationPackage
public class JpaTestConfiguration {

	public final static String AUDITOR_STRING = "Jim Black";


	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> { return Optional.of(AUDITOR_STRING); };
	}
}