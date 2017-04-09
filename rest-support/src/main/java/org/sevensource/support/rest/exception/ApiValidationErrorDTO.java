package org.sevensource.support.rest.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;

import org.sevensource.support.jpa.exception.EntityValidationException;
import org.springframework.http.HttpStatus;

public class ApiValidationErrorDTO extends ApiErrorDTO {
	
	private Map<String, List<String>> validationErrors = new HashMap<>();
	
	public ApiValidationErrorDTO(EntityValidationException ex, HttpStatus status) {
		super(ex, status);
		
		for(ConstraintViolation<?> v : ex.getViolations()) {
			final String key = v.getPropertyPath().toString();
			if(! validationErrors.containsKey(key)) {
				validationErrors.put(key, new ArrayList<>());
			}
			validationErrors.get(key).add(v.getMessage());
		}
	}
	
	public Map<String, List<String>> getValidationErrors() {
		return validationErrors;
	}
}
