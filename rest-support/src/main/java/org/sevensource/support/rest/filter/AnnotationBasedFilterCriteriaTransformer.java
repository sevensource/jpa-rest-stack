package org.sevensource.support.rest.filter;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.springframework.util.ReflectionUtils;

public class AnnotationBasedFilterCriteriaTransformer implements FilterCriteriaTransformer {

	private final Class<?> targetClass;
	private final ConcurrentMap<String, ComparisonFilterOperator[]> cache = new ConcurrentHashMap<>();
	
	public AnnotationBasedFilterCriteriaTransformer(Class<?> target) {
		this.targetClass = target;
	}
	
	@Override
	public boolean isFieldOperationAllowed(String fieldName, ComparisonFilterOperator operation) {
		
		final ComparisonFilterOperator[] operators = cache.computeIfAbsent(fieldName, this::getOperatorsFromAnnotation);
		
		if(operators == null) {
			return false;
		}
		
		for(ComparisonFilterOperator op : operators) {
			if(op.equals(operation)) {
				return true;
			}
		}
		
		return false;
	}
	
	private ComparisonFilterOperator[] getOperatorsFromAnnotation(String fieldName) {
		final Field field = ReflectionUtils.findField(targetClass, fieldName);
		if(field == null) {
			throw new IllegalArgumentException("Field with name " + fieldName + " does not exist on type " + targetClass);
		}
		
		FilterableProperty filterableProperty = field.getAnnotation(FilterableProperty.class);
		return (filterableProperty == null) ? null : filterableProperty.value();
	}
}
