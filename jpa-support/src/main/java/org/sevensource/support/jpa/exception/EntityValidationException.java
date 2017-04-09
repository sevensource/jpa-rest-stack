package org.sevensource.support.jpa.exception;

import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;

public class EntityValidationException extends EntityException {
	private static final long serialVersionUID = 7992904489502842099L;

	private Set<? extends ConstraintViolation<?>> violations;
	
	public EntityValidationException(String message) {
		this(message, Collections.emptySet());
	}
	
//	public EntityValidationException(String message, ConstraintViolation<?> violation) {
//		this(message, new HashSet<>(Arrays.asList(violation)));
//	}
	
//	public EntityValidationException(String message, List<ConstraintViolation<?>> violations) {
//		this(message, new HashSet<>(violations));
//	}
	
	public EntityValidationException(String message, Set<? extends ConstraintViolation<?>> violations) {
		super(message);
		this.violations = violations;
	}

	public Set<? extends ConstraintViolation<?>> getViolations() {
		return violations;
	}
}