package org.sevensource.support.rest.configuration;

import java.util.List;

import org.sevensource.support.rest.exception.RestControllerExceptionHandler;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import({RestControllerExceptionHandler.class})
public class CommonMvcConfiguration implements WebMvcConfigurer {

	private final SpringDataWebProperties properties = new SpringDataWebProperties();

	@Bean
	public SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {
		SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
		sortResolver.setSortParameter(this.properties.getSort().getSortParameter());
		sortResolver.setFallbackSort(Sort.by("id"));
		return sortResolver;
	}

	@Bean
	public PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver() {
		final PageableHandlerMethodArgumentResolver resolver =
				new PageableHandlerMethodArgumentResolver(sortHandlerMethodArgumentResolver());

		resolver.setPageParameterName(properties.getPageable().getPageParameter());
		resolver.setSizeParameterName(properties.getPageable().getSizeParameter());
		resolver.setOneIndexedParameters(false);
		resolver.setPrefix(properties.getPageable().getPrefix());
		resolver.setQualifierDelimiter(properties.getPageable().getQualifierDelimiter());
		resolver.setFallbackPageable(Pageable.unpaged());
		resolver.setMaxPageSize(properties.getPageable().getMaxPageSize());

		return resolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(sortHandlerMethodArgumentResolver());
		argumentResolvers.add(pageableHandlerMethodArgumentResolver());
	}
}
