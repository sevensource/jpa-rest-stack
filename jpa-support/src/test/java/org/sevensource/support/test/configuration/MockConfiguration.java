package org.sevensource.support.test.configuration;

import org.sevensource.support.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.model.mock.UUIDTestEntityMockProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MockFactoryConfiguration.class)
@ComponentScan(basePackageClasses=UUIDTestEntityMockProvider.class)
public class MockConfiguration {

}
