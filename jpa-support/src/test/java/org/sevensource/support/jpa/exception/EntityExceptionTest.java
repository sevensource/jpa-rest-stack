package org.sevensource.support.jpa.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntityExceptionTest {

	private static Validator validator;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation
				.byDefaultProvider()
				.configure()
				.messageInterpolator(new ParameterMessageInterpolator())
				.buildValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void entityNotFoundException() {
		String msg = UUID.randomUUID().toString();
		EntityNotFoundException e = new EntityNotFoundException(msg);
		assertThat(e.getMessage()).isEqualTo(msg);
	}
	
	@Test
	public void entityAlreadyExistsException() {
		String msg = UUID.randomUUID().toString();
		EntityAlreadyExistsException e = new EntityAlreadyExistsException(msg);
		assertThat(e.getMessage()).isEqualTo(msg);
	}
	
	static class ToBeValidated {
		@NotNull
		private String name;
	}
	
	@Test
	public void entityValidationException() {
		String msg = UUID.randomUUID().toString();
		Set<ConstraintViolation<ToBeValidated>> violations = validator.validate(new ToBeValidated());
		
		EntityValidationException e = new EntityValidationException(msg, violations);
		assertThat(e.getMessage()).isEqualTo(msg);
		assertThat(e.getViolations()).hasSize(violations.size());
	}
	
	@Test
	public void entityValidationException2() {
		String msg = UUID.randomUUID().toString();
		
		EntityValidationException e = new EntityValidationException(msg);
		assertThat(e.getMessage()).isEqualTo(msg);
		assertThat(e.getViolations()).isEmpty();
	}
	
}
