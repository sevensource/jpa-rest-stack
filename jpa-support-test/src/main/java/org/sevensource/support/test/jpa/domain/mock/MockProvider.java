package org.sevensource.support.test.jpa.domain.mock;

import java.util.List;

import org.sevensource.support.jpa.domain.PersistentEntity;

public interface MockProvider<T extends PersistentEntity<?>> {
	
	public Class<T> getDomainClass();
	
	public List<T> create(int count);
	public T create();
	
	public T populate();
	public T touch(T entity);
	
	public List<Class<?>> getDeletionOrder();
}