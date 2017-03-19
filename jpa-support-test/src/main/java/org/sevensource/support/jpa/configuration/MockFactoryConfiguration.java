package org.sevensource.support.jpa.configuration;

import org.sevensource.support.jpa.model.mock.MockFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockFactoryConfiguration {
	
	@Bean
	public MockFactory<?> mockFactory() {
		return new MockFactory<>();
	}
}
