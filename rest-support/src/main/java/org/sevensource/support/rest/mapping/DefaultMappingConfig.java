package org.sevensource.support.rest.mapping;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
		componentModel="spring",
		unmappedTargetPolicy=ReportingPolicy.ERROR
		)
public interface DefaultMappingConfig {
	
}
