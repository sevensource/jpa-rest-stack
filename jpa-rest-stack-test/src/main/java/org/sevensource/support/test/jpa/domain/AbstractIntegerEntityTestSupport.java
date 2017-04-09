package org.sevensource.support.test.jpa.domain;

import java.security.SecureRandom;

import org.sevensource.support.jpa.domain.AbstractIntegerEntity;

public abstract class AbstractIntegerEntityTestSupport<E extends AbstractIntegerEntity> extends AbstractPersistentEntityTestSupport<Integer, E> {

	private SecureRandom random = new SecureRandom();
	
	protected AbstractIntegerEntityTestSupport(Class<E> domainClass) {
		super(domainClass);
	}
	
	@Override
	protected Integer getNewId() {
		return random.nextInt();
	}
	
	@Override
	protected boolean entityChangesHashCodeAfterPersist() {
		return true;
	}

}
