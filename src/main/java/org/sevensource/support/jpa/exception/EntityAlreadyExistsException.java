package org.sevensource.support.jpa.exception;

public class EntityAlreadyExistsException extends EntityException {

	public EntityAlreadyExistsException() {
		this("Entity already exists");
	}

	public EntityAlreadyExistsException(String message) {
		this(message, null);
	}

	public EntityAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
