package org.sevensource.support.test.rest.configuration;

import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.configuration.CommonMvcConfiguration;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CommonMvcConfiguration.class, MockFactoryConfiguration.class, CommonMappingConfiguration.class})
public class RestTestConfiguration {

}
