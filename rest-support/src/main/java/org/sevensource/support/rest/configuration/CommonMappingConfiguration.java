package org.sevensource.support.rest.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.sevensource.support.rest.mapping.ReferenceDTOToEntityConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonMappingConfiguration {
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper
			.getConfiguration()
			.setAmbiguityIgnored(false)
			.setMatchingStrategy(MatchingStrategies.STRICT);
		
		return modelMapper;
	}
	
	@Bean
	public ReferenceDTOToEntityConverter referenceDTOToEntityConverter() {
		return new ReferenceDTOToEntityConverter();
	}
}
