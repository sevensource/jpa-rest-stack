package org.sevensource.support.rest.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.Test;
import org.sevensource.support.jpa.filter.ComparisonFilterCriteria;
import org.sevensource.support.jpa.filter.ComparisonFilterOperator;
import org.springframework.core.convert.support.DefaultConversionService;

public class AnnotationBasedFilterCriteriaTransformerTest {

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
	
	private static enum Gender {
		MALE, FEMALE;
	}
	
	private static class DifferentTypesDTO {
		private String stringProperty;
		private Integer integerProperty;
		private UUID uuidProperty;
		private Instant instantProperty;
		private Gender enumProperty;
	}
	
	@Test
	public void default_value_asserts_true() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DefaultValueDTO.class, new DefaultConversionService());
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isTrue();
	}
	
	@Test
	public void no_annotation_asserts_false() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DefaultValueDTO.class, new DefaultConversionService());
		assertThat(transformer.isFieldOperationAllowed("firstname", ComparisonFilterOperator.GREATER_THAN)).isFalse();
	}
	
	@Test
	public void invalid_field_throws() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DefaultValueDTO.class, new DefaultConversionService());
		
		assertThatThrownBy(() -> 
			transformer.isFieldOperationAllowed("someprop", ComparisonFilterOperator.GREATER_THAN)).isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void single_value_asserts_true() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(SingleValueDTO.class, new DefaultConversionService());
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isTrue();
	}
	
	@Test
	public void single_value_asserts_false() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(SingleValueDTO.class, new DefaultConversionService());
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.GREATER_THAN)).isFalse();
	}
	
	@Test
	public void no_value_works() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(NoValuesDTO.class, new DefaultConversionService());
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isFalse();
	}
	
	@Test
	public void multi_value_asserts_true() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(ManyValuesDTO.class, new DefaultConversionService());
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.EQUAL_TO)).isTrue();
	}
	
	@Test
	public void multi_value_asserts_false() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(ManyValuesDTO.class, new DefaultConversionService());
		assertThat(transformer.isFieldOperationAllowed("name", ComparisonFilterOperator.LIKE)).isFalse();
	}
	
	@Test
	public void string_property_null_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("stringProperty", ComparisonFilterOperator.EQUAL_TO, null)))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isNull();
	}
	
	@Test
	public void string_property_integer_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("stringProperty", ComparisonFilterOperator.EQUAL_TO, 1)))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isEqualTo("1");
	}
	
	@Test
	public void integer_property_null_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("integerProperty", ComparisonFilterOperator.EQUAL_TO, null)))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isNull();
	}
	
	@Test
	public void integer_property_string_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("integerProperty", ComparisonFilterOperator.EQUAL_TO, "1")))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isEqualTo(1);
	}
	
	@Test
	public void integer_property_empty_string_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("integerProperty", ComparisonFilterOperator.EQUAL_TO, "")))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isNull();
	}
	
	@Test
	public void uuid_property_string_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		UUID uuid = UUID.randomUUID();
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("uuidProperty", ComparisonFilterOperator.EQUAL_TO, uuid.toString())))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isEqualTo(uuid);
	}
	
	@Test
	public void uuid_property_null_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("uuidProperty", ComparisonFilterOperator.EQUAL_TO, null)))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isNull();
	}
	
	@Test
	public void uuid_property_empty_string_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("uuidProperty", ComparisonFilterOperator.EQUAL_TO, "")))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isNull();
	}
	
	@Test
	public void instant_property_empty_string_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("instantProperty", ComparisonFilterOperator.EQUAL_TO, "")))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isNull();
	}
	
	@Test
	public void instant_property_string_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("instantProperty", ComparisonFilterOperator.EQUAL_TO, "1")))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isEqualTo(Instant.ofEpochMilli(1));
	}
	
	@Test
	public void instant_property_integer_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("instantProperty", ComparisonFilterOperator.EQUAL_TO, 1)))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isEqualTo(Instant.ofEpochMilli(1));
	}
	
	@Test
	public void instant_property_long_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("instantProperty", ComparisonFilterOperator.EQUAL_TO, 1L)))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isEqualTo(Instant.ofEpochMilli(1));
	}
	
	@Test
	public void enum_property_string_value_transformation() {
		AnnotationBasedFilterCriteriaTransformer transformer = 
				new AnnotationBasedFilterCriteriaTransformer(DifferentTypesDTO.class, new DefaultConversionService());
		
		assertThat(transformer.transform(new ComparisonFilterCriteria("enumProperty", ComparisonFilterOperator.EQUAL_TO, "MALE")))
			.isExactlyInstanceOf(ComparisonFilterCriteria.class)
			.extracting(c -> ((ComparisonFilterCriteria)c).getValue())
			.isEqualTo(Gender.MALE);
	}
	
}
