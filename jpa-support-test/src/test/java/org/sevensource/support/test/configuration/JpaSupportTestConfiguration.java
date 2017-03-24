package org.sevensource.support.test.configuration;

import org.sevensource.support.test.jdbc.configuration.ProxyDatasourceConfiguration;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.jpa.configuration.StaticJpaAuditorAwareConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@Import({
		StaticJpaAuditorAwareConfiguration.class,
		MockFactoryConfiguration.class,
		ProxyDatasourceConfiguration.class})
@EnableJpaAuditing
public class JpaSupportTestConfiguration {

}
