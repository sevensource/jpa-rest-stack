package org.sevensource.support.rest.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;

public class DefaultAnnotationBasedFilterCriteriaTransformerTest {

	private static class DefaultValueDTO {
		@FilterableProperty
		private String name;
		private String firstname;
	}
	
	private static class SingleValueDTO {
		@FilterableProperty(ComparisonFilterOperator.EQUAL_TO)
		private String name;
	}
	
	private static class NoValuesDTO {
		@FilterableProperty({})
		private String name;
	}
	
	private static class ManyValuesDTO {
		@FilterableProperty({ComparisonFilterOperator.EQUAL_TO, ComparisonFilterOperator.GREATER_THAN})
		private String name;
	}
	
	@Test
	public void default_value_asserts_true() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(DefaultValueDTO.class);
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isTrue();
	}
	
	@Test
	public void no_annotation_asserts_false() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(DefaultValueDTO.class);
		assertThat(transformer.isFieldOperationAllowed("firstname", ComparisonFilterOperator.GREATER_THAN)).isFalse();
	}
	
	@Test
	public void invalid_field_throws() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(DefaultValueDTO.class);
		
		assertThatThrownBy(() -> 
			transformer.isFieldOperationAllowed("someprop", ComparisonFilterOperator.GREATER_THAN)).isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void single_value_asserts_true() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(SingleValueDTO.class);
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isTrue();
	}
	
	@Test
	public void single_value_asserts_false() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(SingleValueDTO.class);
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.GREATER_THAN)).isFalse();
	}
	
	@Test
	public void no_value_works() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(NoValuesDTO.class);
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isFalse();
	}
	
	@Test
	public void multi_value_asserts_true() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(ManyValuesDTO.class);
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isTrue();
	}
	
	@Test
	public void multi_value_asserts_false() {
		DefaultAnnotationBasedFilterCriteriaTransformer transformer = 
				new DefaultAnnotationBasedFilterCriteriaTransformer(ManyValuesDTO.class);
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.LIKE)).isFalse();
	}
}
