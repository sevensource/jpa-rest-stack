package org.sevensource.support.jpa.exception;

import org.springframework.core.NestedRuntimeException;

public abstract class EntityException extends NestedRuntimeException {
	private static final long serialVersionUID = 7992904489502842099L;

	public EntityException(String message) {
		super(message);
	}
}
