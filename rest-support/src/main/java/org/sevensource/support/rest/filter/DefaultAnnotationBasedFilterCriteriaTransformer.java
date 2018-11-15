package org.sevensource.support.rest.filter;

import java.lang.reflect.Field;

import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.springframework.util.ReflectionUtils;

public class DefaultAnnotationBasedFilterCriteriaTransformer implements FilterCriteriaTransformer {

	private final Class<?> targetClass;
	
	public DefaultAnnotationBasedFilterCriteriaTransformer(Class<?> target) {
		this.targetClass = target;
	}
	
	@Override
	public boolean isFieldOperationAllowed(String fieldName, ComparisonFilterOperator operation) {
		final Field field = ReflectionUtils.findField(targetClass, fieldName);
		if(field == null) {
			throw new IllegalArgumentException("Field with name " + fieldName + " does not exist on type " + targetClass);
		}
		
		FilterableProperty filterableProperty = field.getAnnotation(FilterableProperty.class);
		
		if(filterableProperty == null) {
			return false;
		}
		
		for(ComparisonFilterOperator op : filterableProperty.value()) {
			if(op.equals(operation)) {
				return true;
			}
		}
		
		return false;
	}
}
