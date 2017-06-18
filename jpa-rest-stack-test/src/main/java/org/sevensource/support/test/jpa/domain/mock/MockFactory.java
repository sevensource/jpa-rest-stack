package org.sevensource.support.test.jpa.domain.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class MockFactory {
	private Map<Class<?>, MockProvider<?>> factories = new HashMap<Class<?>, MockProvider<?>>();
	
	@Autowired(required=false)
	void setMockProvider(List<MockProvider<?>> providers) {
		Assert.notNull(providers, "Providers cannot be null");
		for(MockProvider<?> p : providers) {
			addMockProvider(p);
		}
	}
	
	public void addMockProvider(MockProvider<?> provider) {
		factories.put(provider.getDomainClass(), provider);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PersistentEntity<?>> MockProvider<T> on(Class<T> domainClass) {
		Assert.notNull(domainClass, "domainClass must not be null");
		MockProvider<?> factory = factories.get(domainClass);
		if (factory == null) {
			throw new IllegalArgumentException("No provider registered for class " + domainClass.getName());
		}
		return (MockProvider<T>) factory;
	}
}
