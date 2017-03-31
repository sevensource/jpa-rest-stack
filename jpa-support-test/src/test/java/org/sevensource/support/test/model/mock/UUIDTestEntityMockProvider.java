package org.sevensource.support.test.model.mock;

import java.util.Arrays;
import java.util.List;

import org.sevensource.support.test.jpa.model.mock.AbstractMockProvider;
import org.sevensource.support.test.jpa.model.mock.MockFactory;
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
	
	@Override
	public List<Class<?>> getDeletionOrder() {
		return Arrays.asList(UUIDTestEntity.class, UUIDTestReferenceEntity.class);
	}
}
