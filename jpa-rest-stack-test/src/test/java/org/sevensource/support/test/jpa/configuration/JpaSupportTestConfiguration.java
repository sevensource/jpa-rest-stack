package org.sevensource.support.test.jpa.configuration;

import org.sevensource.support.test.jdbc.configuration.ProxyDatasourceConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@Import({
		StaticJpaAuditorAwareConfiguration.class,
		MockFactoryConfiguration.class,
		ProxyDatasourceConfiguration.class})
@EnableJpaAuditing
@AutoConfigurationPackage
public class JpaSupportTestConfiguration {

}
