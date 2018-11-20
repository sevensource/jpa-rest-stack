package org.sevensource.support.rest.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.springframework.core.annotation.AnnotationUtils;

public class FilterablePropertyTest {

	@Test
	public void ensure_all_operators_are_default_values() {
		Object defaultValue = AnnotationUtils.getDefaultValue(FilterableProperty.class);
		ComparisonFilterOperator[] defaultOperators = (ComparisonFilterOperator[]) defaultValue;
		
		assertThat(defaultOperators).containsExactly(ComparisonFilterOperator.values());
	}

}
