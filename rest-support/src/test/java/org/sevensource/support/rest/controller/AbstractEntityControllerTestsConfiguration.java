package org.sevensource.support.rest.controller;

import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.configuration.CommonMvcConfiguration;
import org.sevensource.support.rest.mapping.SimpleTestEntityMapperImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CommonMvcConfiguration.class,
		CommonMappingConfiguration.class,
		SimpleTestEntityRestController.class,
		SimpleTestEntityMapperImpl.class})
class AbstractEntityControllerTestsConfiguration {

}
