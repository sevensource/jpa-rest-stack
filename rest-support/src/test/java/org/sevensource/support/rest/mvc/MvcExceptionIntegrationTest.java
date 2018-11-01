package org.sevensource.support.rest.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.rest.configuration.CommonMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Import({CommonMvcConfiguration.class})

@SpringBootApplication(scanBasePackageClasses={}, exclude={
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
		})
@Configuration
public class MvcExceptionIntegrationTest {

	private final static String TEXT = "Some detail message";

	@Autowired
	TestRestTemplate restTemplate;

	@RestController
	static class ExceptionThrowingController {
		class ValidationPojo {
			@NotNull
			String title;

			@Pattern(regexp="[0-9]+")
			@NotEmpty
			String anumber = "";
		}

		@Autowired
		Validator validator;

		@RequestMapping("/alreadyExistsException")
		String alreadyExists() {
			throw new EntityAlreadyExistsException(TEXT);
		}

		@RequestMapping("/notFoundException")
		String notFound() {
			throw new EntityNotFoundException(TEXT);
		}

		@RequestMapping("/validationSimple")
		String validationSimple() {
			throw new EntityValidationException(TEXT);
		}

		@RequestMapping("/validationExtended")
		String validationExtended() {
			Set<? extends ConstraintViolation<?>> violations = validator.validate(new ValidationPojo());
			assertThat(violations).isNotEmpty();
			throw new EntityValidationException(TEXT, violations);
		}
	}


	@Test
	public void alreadyExistsException_should_return_409() {
		ResponseEntity<String> response = restTemplate.getForEntity("/alreadyExistsException", String.class);
		DocumentContext ctx = JsonPath.parse(response.getBody());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(ctx.read("$.message", String.class)).isEqualTo(TEXT);
	}

	@Test
	public void notFoundException_should_return_404() {
		ResponseEntity<String> response = restTemplate.getForEntity("/notFoundException", String.class);
		DocumentContext ctx = JsonPath.parse(response.getBody());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(ctx.read("$.message", String.class)).isEqualTo(TEXT);
	}

	@Test
	public void entitValidationException_should_return_422() {
		ResponseEntity<String> response = restTemplate.getForEntity("/validationSimple", String.class);
		DocumentContext ctx = JsonPath.parse(response.getBody());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(ctx.read("$.message", String.class)).isEqualTo(TEXT);
	}

	@Test
	public void entitValidationExceptionWithViolations_should_return_422_and_contain_info() {
		ResponseEntity<String> response = restTemplate.getForEntity("/validationExtended", String.class);
		DocumentContext ctx = JsonPath.parse(response.getBody());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(ctx.read("$.message", String.class)).isEqualTo(TEXT);
		assertThat(ctx.read("$.validationErrors", Map.class)).containsOnlyKeys("title", "anumber");
		assertThat(ctx.read("$.validationErrors.title", List.class)).hasSize(1);
		assertThat(ctx.read("$.validationErrors.anumber", List.class)).hasSize(2);
	}
}
