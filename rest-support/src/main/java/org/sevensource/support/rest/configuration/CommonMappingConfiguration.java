package org.sevensource.support.rest.configuration;

import org.sevensource.support.rest.mapping.ReferenceDTOEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonMappingConfiguration {
	
	@Bean
	public ReferenceDTOEntityMapper referenceDTOEntityMapper() {
		return new ReferenceDTOEntityMapper();
	}
}
