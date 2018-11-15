package org.sevensource.support.rest.filter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.sevensource.support.jpa.filter.ComparisonFilterOperator;

@Retention(RUNTIME)
@Target(FIELD)
public @interface FilterableProperty {

	ComparisonFilterOperator[] value() default {
		ComparisonFilterOperator.LIKE,
		ComparisonFilterOperator.NOT_LIKE,
		ComparisonFilterOperator.IN,
		ComparisonFilterOperator.NOT_IN,
		ComparisonFilterOperator.EQUAL_TO,
		ComparisonFilterOperator.NOT_EQUAL_TO,
		ComparisonFilterOperator.LESS_THAN,
		ComparisonFilterOperator.GREATER_THAN,
		ComparisonFilterOperator.LESS_THAN_OR_EQUAL,
		ComparisonFilterOperator.GREATER_THAN_OR_EQUAL
	};
}