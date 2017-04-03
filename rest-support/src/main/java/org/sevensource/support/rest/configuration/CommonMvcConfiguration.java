package org.sevensource.support.rest.configuration;

import java.util.List;

import org.sevensource.support.rest.exception.RestControllerExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Import({RestControllerExceptionHandler.class})
public class CommonMvcConfiguration extends WebMvcConfigurerAdapter {

	@Bean
	public SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {
		SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
		sortResolver.setFallbackSort(new Sort( new Sort.Order("id") ));
		return sortResolver;
	}
	
	@Bean
	public PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver() {
		SortHandlerMethodArgumentResolver sortResolver = sortHandlerMethodArgumentResolver();
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver(sortResolver);
		resolver.setFallbackPageable(null);
		return resolver;
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(sortHandlerMethodArgumentResolver());
		argumentResolvers.add(pageableHandlerMethodArgumentResolver());
	}
}
