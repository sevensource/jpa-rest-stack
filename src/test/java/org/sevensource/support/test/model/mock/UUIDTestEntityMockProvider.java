package org.sevensource.support.test.model.mock;

import java.util.UUID;

import org.sevensource.support.jpa.model.mock.AbstractMockProvider;
import org.sevensource.support.jpa.model.mock.MockFactory;
import org.sevensource.support.test.model.UUIDTestEntity;
import org.sevensource.support.test.model.UUIDTestReferenceEntity;
import org.springframework.stereotype.Component;

@Component
public class UUIDTestEntityMockProvider extends AbstractMockProvider<UUIDTestEntity> {
	
	private final static String NAME = "MOCK";
	private int seed = 0;
	
	public UUIDTestEntityMockProvider() {
		super(UUIDTestEntity.class);
	}
	
	@Override
	public UUIDTestEntity populate() {
		UUIDTestEntity e = new UUIDTestEntity(NAME + seed++);
		UUIDTestReferenceEntity ref = MockFactory.on(UUIDTestReferenceEntity.class).create();
		e.setRef(ref);
		return e;
	}

	@Override
	public UUIDTestEntity touch(UUIDTestEntity entity) {
		entity.setTitle(NAME + seed++);
		return entity;
	}
}
