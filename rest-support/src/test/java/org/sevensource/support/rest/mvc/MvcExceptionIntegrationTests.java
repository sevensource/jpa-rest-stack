package org.sevensource.support.rest.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.rest.configuration.CommonMappingConfiguration;
import org.sevensource.support.rest.configuration.CommonMvcConfiguration;
import org.sevensource.support.rest.controller.TestEntityRestController;
import org.sevensource.support.rest.controller.TestEntityRestController.TestEntity;
import org.sevensource.support.rest.exception.ApiErrorDTO;
import org.sevensource.support.rest.exception.ApiValidationErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Import({CommonMvcConfiguration.class, CommonMappingConfiguration.class, TestEntityRestController.class})
@SpringBootApplication(exclude={
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
		})
public class MvcExceptionIntegrationTests {

	@MockBean
	private EntityService<TestEntity, UUID> service;
	
	@Autowired
	TestRestTemplate restTemplate;
	
	@Autowired
	Validator validator;
	
	private static String url(String path) {
		return TestEntityRestController.PATH + path;
	}
	
	private final static RequestEntity<String> SIMPLE_POST_REQUESTENTITY = RequestEntity
			.post(URI.create(url("")))
			.contentType(MediaType.APPLICATION_JSON)
			.body("{\"name\":	\"\"}");
	
	private <T extends ApiErrorDTO> ResponseEntity<T> simple_error_tests(Class<T> clazz, RequestEntity<?> request, HttpStatus expected) {
		
		Instant BEGIN = Instant.now();
		
		ResponseEntity<T> response = restTemplate.exchange(request, clazz);
		
		assertThat(response.getStatusCode()).isEqualTo(expected);
		assertThat(response.getBody().getError()).isNotBlank();
		assertThat(response.getBody().getException()).isNotBlank();
		assertThat(response.getBody().getMessage()).isNotBlank();
		assertThat(response.getBody().getPath()).isNotBlank();
		assertThat(response.getBody().getStatus()).isEqualTo(expected.value());
		assertThat(response.getBody().getTimestamp()).isBetween(BEGIN.toEpochMilli(), Instant.now().toEpochMilli());
		return response;
	}
	
	
	class ValidatorDTO {
		@NotNull
		String name = null;
		
		@Pattern(regexp="[0-9]+")
		String name2 = "abc";
	}
	
	@Test
	public void post_resource_with_validation_error() throws Exception {
		ArrayList<ConstraintViolation<?>> violations = new ArrayList(validator.validate(new ValidatorDTO()));
		
		when(service.create(any())).thenThrow(new EntityValidationException("entity validation failed", violations));
		simple_error_tests(ApiErrorDTO.class, SIMPLE_POST_REQUESTENTITY, HttpStatus.UNPROCESSABLE_ENTITY);
		
		ResponseEntity<ApiValidationErrorDTO> response = simple_error_tests(ApiValidationErrorDTO.class, SIMPLE_POST_REQUESTENTITY, HttpStatus.UNPROCESSABLE_ENTITY);
		
		Map<String, List<String>> validationErrors = response.getBody().getValidationErrors();
		assertThat(validationErrors).containsKey("name");
	}
	
	@Test
	public void post_resource_with_already_exists() throws Exception {
		when(service.create(any())).thenThrow(new EntityAlreadyExistsException("Entity already exists at id 1234"));
		simple_error_tests(ApiErrorDTO.class, SIMPLE_POST_REQUESTENTITY, HttpStatus.CONFLICT);
	}
	
	@Test
	public void get_resource_with_entity_not_found() throws Exception {
		when(service.get(any())).thenThrow(new EntityNotFoundException("Entity does not exist"));
		RequestEntity<Void> re = RequestEntity
			.get(URI.create(url("/" + UUID.randomUUID())))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.build();
		
		simple_error_tests(ApiValidationErrorDTO.class, re, HttpStatus.NOT_FOUND);
	}
}
