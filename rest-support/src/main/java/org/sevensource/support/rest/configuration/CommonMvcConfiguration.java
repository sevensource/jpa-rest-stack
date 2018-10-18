package org.sevensource.support.rest.configuration;

import org.sevensource.support.rest.exception.RestControllerExceptionHandler;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({RestControllerExceptionHandler.class})
public class CommonMvcConfiguration implements WebMvcConfigurer {

	@Bean
	public SortHandlerMethodArgumentResolverCustomizer sortHandlerMethodArgumentResolverCustomizer() {
		return (resolver) -> {
			resolver.setFallbackSort(Sort.by("id"));
		};
	}

	@Bean
	public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
		final SpringDataWebProperties properties = new SpringDataWebProperties();

		return (resolver) -> {
			resolver.setPageParameterName(properties.getPageable().getPageParameter());
			resolver.setSizeParameterName(properties.getPageable().getSizeParameter());
			resolver.setOneIndexedParameters(false);
			resolver.setPrefix(properties.getPageable().getPrefix());
			resolver.setQualifierDelimiter(properties.getPageable().getQualifierDelimiter());
			resolver.setFallbackPageable(Pageable.unpaged());
			resolver.setMaxPageSize(properties.getPageable().getMaxPageSize());
		};
	}
}
