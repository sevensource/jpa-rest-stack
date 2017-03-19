package org.sevensource.support.test.jpa.configuration;

import org.sevensource.support.test.jpa.model.mock.MockFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockFactoryConfiguration {
	
	@Bean
	public MockFactory<?> mockFactory() {
		return new MockFactory<>();
	}
}
