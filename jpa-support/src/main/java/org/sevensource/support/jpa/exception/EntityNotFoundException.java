package org.sevensource.support.jpa.exception;

public class EntityNotFoundException extends EntityException {
	private static final long serialVersionUID = 7992904489502842099L;

	public EntityNotFoundException() {
		this("Entity does not exist");
	}

	public EntityNotFoundException(String message) {
		this(message, null);
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
