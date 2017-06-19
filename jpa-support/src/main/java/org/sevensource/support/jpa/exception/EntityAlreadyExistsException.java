package org.sevensource.support.jpa.exception;

public class EntityAlreadyExistsException extends EntityException {

	private static final long serialVersionUID = 904694652466531283L;

	public EntityAlreadyExistsException(String message) {
		super(message);
	}
}
