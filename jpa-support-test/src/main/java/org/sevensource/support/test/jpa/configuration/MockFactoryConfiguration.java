package org.sevensource.support.test.jpa.configuration;

import org.sevensource.support.test.jpa.domain.mock.MockFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockFactoryConfiguration {
	
	@Bean
	public MockFactory<?> mockFactory() {
		return new MockFactory<>();
	}
}
