package org.sevensource.support.test.model.mock;

import org.sevensource.support.jpa.model.mock.AbstractMockProvider;
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
}
