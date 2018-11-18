package org.sevensource.support.rest.filter;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.sevensource.support.jpa.filter.FilterCriteria;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class AnnotationBasedFilterCriteriaTransformer implements FilterCriteriaTransformer {

	private final Class<?> targetClass;
	private final ConversionService conversionService;
	private final ConcurrentMap<String, ComparisonFilterOperator[]> operatorCache = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, Class<?>> propertyTypeCache = new ConcurrentHashMap<>();
	
	public AnnotationBasedFilterCriteriaTransformer(Class<?> target, ConversionService conversionService) {
		this.targetClass = target;
		this.conversionService = conversionService;
	}
	
	@Override
	public FilterCriteria transform(ComparisonFilterCriteria criteria) {
		if(criteria.getValue() == null) {
			return criteria;
		}
		
		final Class<?> propertyType = propertyTypeCache.computeIfAbsent(criteria.getKey(), this::getPropertyTypeByPropertyName);
		
		if(propertyType.isAssignableFrom(criteria.getValue().getClass())) {
			return criteria;
		} else {
			Object convertedValue = convertValue(criteria.getValue(), propertyType);
			return criteria.withNewValue(convertedValue);
		}
	}
	
	@Override
	public boolean isFieldOperationAllowed(String fieldName, ComparisonFilterOperator operation) {
		
		final ComparisonFilterOperator[] operators = operatorCache.computeIfAbsent(fieldName, this::getOperatorsFromAnnotation);
		
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
	
	private Object convertValue(Object value, Class<?> targetJavaType) {
		
		if(targetJavaType.equals(Instant.class)) {
			return convertToInstant(value);
		}
		
		final TypeDescriptor sourceTypeDescriptor = TypeDescriptor.forObject(value);
		
		TypeDescriptor targetTypeDescriptor;
		if(value instanceof List) {
			targetTypeDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetJavaType));
		} else {
			targetTypeDescriptor = TypeDescriptor.valueOf(targetJavaType);
		}
		
		return conversionService.convert(value, sourceTypeDescriptor, targetTypeDescriptor);
	}
	
	private Class<?> getPropertyTypeByPropertyName(String propertyName) {
		final Field field = ReflectionUtils.findField(targetClass, propertyName);
		if(field == null) {
			throw new IllegalArgumentException("Field with name " + propertyName + " does not exist on type " + targetClass);
		}
		
		return field.getType();
	}
	
	private ComparisonFilterOperator[] getOperatorsFromAnnotation(String fieldName) {
		final Field field = ReflectionUtils.findField(targetClass, fieldName);
		if(field == null) {
			throw new IllegalArgumentException("Field with name " + fieldName + " does not exist on type " + targetClass);
		}
		
		FilterableProperty filterableProperty = field.getAnnotation(FilterableProperty.class);
		return (filterableProperty == null) ? null : filterableProperty.value();
	}
	
	private Instant convertToInstant(Object value) {
		
		if(value == null || StringUtils.isEmpty(value)) {
			return null;
		}
		
		try {
			return conversionService.convert(value, Instant.class);
		} catch(ConverterNotFoundException e) {
			Long longValue = conversionService.convert(value, Long.class);
			if(longValue == null) {
				return null;
			} else {
				return Instant.ofEpochMilli(longValue);
			}
		}
	}
}
