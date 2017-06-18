package org.sevensource.support.test.rest.controller;

import java.io.Serializable;
import java.util.UUID;

import org.sevensource.support.jpa.domain.PersistentEntity;

public abstract class UUIDEntityRestControllerTestSupport<E extends PersistentEntity<UUID>> extends EntityRestControllerTestSupport<E, UUID> {

	@Override
	protected UUID nextId() {
		return UUID.randomUUID();
	}

	@Override
	protected UUID nillId() {
		return new UUID(0, 0);
	}

	@Override
	protected Serializable invalidId() {
		return "abc";
	}
	
}
