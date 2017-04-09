package org.sevensource.support.test.jpa.domain;

import java.util.UUID;

import org.sevensource.support.jpa.domain.AbstractUUIDEntity;

public abstract class AbstractUUIDEntityTestSupport<E extends AbstractUUIDEntity> extends AbstractPersistentEntityTestSupport<UUID, E> {

	protected AbstractUUIDEntityTestSupport(Class<E> domainClass) {
		super(domainClass);
	}
	
	@Override
	protected UUID getNewId() {
		return UUID.randomUUID();
	}

	@Override
	protected boolean entityChangesHashCodeAfterPersist() {
		return false;
	}
}
