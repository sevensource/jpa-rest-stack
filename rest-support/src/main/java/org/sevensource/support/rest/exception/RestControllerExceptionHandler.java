package org.sevensource.support.rest.exception;

import javax.servlet.http.HttpServletRequest;

import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler {

	private static final String ERROR_URI_KEY = "javax.servlet.error.request_uri";
	
	
	@ExceptionHandler({
		EntityAlreadyExistsException.class,
		EntityNotFoundException.class
	})
	public ResponseEntity<ApiErrorDTO> handleEntityException(EntityException e, HttpServletRequest request) {
		HttpStatus status;
		
		if(e instanceof EntityAlreadyExistsException)
			status = HttpStatus.CONFLICT;
		else if(e instanceof EntityNotFoundException)
			status = HttpStatus.NOT_FOUND;
		else
			throw new IllegalStateException("Don't know how to handle exception of type " + e.getClass().getSimpleName(), e);
		
		ApiErrorDTO dto = new ApiErrorDTO(e, status);
		return handle(dto, request);
	}
	
	
	@ExceptionHandler({EntityValidationException.class})
	public ResponseEntity<ApiErrorDTO> handleEntityValidationException(EntityValidationException e, HttpServletRequest request) {
		ApiValidationErrorDTO dto = new ApiValidationErrorDTO(e, HttpStatus.UNPROCESSABLE_ENTITY);
		return handle(dto, request);
	}
	
	private ResponseEntity<ApiErrorDTO> handle(ApiErrorDTO dto, HttpServletRequest request) {
		String path = (String) request.getAttribute(ERROR_URI_KEY); 
		
		if(path != null) {
			dto.setPath(path);
		} else {
			path = request.getRequestURI();
			dto.setPath(path);
		}
		
		return ResponseEntity
				.status(dto.getStatus())
				.body(dto);
	}
}
