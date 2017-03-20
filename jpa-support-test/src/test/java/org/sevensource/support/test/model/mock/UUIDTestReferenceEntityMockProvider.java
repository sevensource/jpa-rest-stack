package org.sevensource.support.test.model.mock;

import java.util.Arrays;
import java.util.List;

import org.sevensource.support.test.jpa.model.mock.AbstractMockProvider;
import org.sevensource.support.test.model.UUIDTestReferenceEntity;
import org.springframework.stereotype.Component;

@Component
public class UUIDTestReferenceEntityMockProvider extends AbstractMockProvider<UUIDTestReferenceEntity> {
	
	public UUIDTestReferenceEntityMockProvider() {
		super(UUIDTestReferenceEntity.class);
	}
	
	@Override
	public UUIDTestReferenceEntity populate() {
		UUIDTestReferenceEntity e = new UUIDTestReferenceEntity();
		return e;
	}

	@Override
	public UUIDTestReferenceEntity touch(UUIDTestReferenceEntity entity) {
		return entity;
	}
	
	@Override
	public List<Class<?>> getDeletionOrder() {
		return Arrays.asList(UUIDTestReferenceEntity.class);
	}
}
