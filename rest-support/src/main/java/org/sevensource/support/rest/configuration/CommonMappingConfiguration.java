package org.sevensource.support.rest.configuration;

import org.sevensource.support.rest.mapping.ReferenceDTOEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonMappingConfiguration {
	
//	@Bean
//	public ModelMapper modelMapper() {
//		ModelMapper modelMapper = new ModelMapper();
//		modelMapper
//			.getConfiguration()
//			.setAmbiguityIgnored(false)
//			.setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE)
//			.setMatchingStrategy(MatchingStrategies.STRICT);
//		
//		return modelMapper;
//	}
	
//	@Bean
//	public ReferenceDTOToEntityConverter referenceDTOToEntityConverter() {
//		return new ReferenceDTOToEntityConverter();
//	}
	
	@Bean
	public ReferenceDTOEntityMapper referenceDTOEntityMapper() {
		return new ReferenceDTOEntityMapper();
	}
}
