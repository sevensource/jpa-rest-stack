package org.sevensource.support.jpa.exception;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.google.common.collect.Sets;

public class EntityValidationException extends EntityException {
	private static final long serialVersionUID = 7992904489502842099L;

	private Set<ConstraintViolation<?>> violations = Collections.emptySet();
	
	public EntityValidationException(String message) {
		super(message);
	}
	
	public EntityValidationException(String message, List<ConstraintViolation<?>> violations) {
		super(message);
		this.violations = Sets.newHashSet(violations);
	}
	
	public EntityValidationException(String message, ConstraintViolation<?> violation) {
		super(message);
		this.violations = Sets.newHashSet(violation);
	}
	
	public Set<ConstraintViolation<?>> getViolations() {
		return violations;
	}
}