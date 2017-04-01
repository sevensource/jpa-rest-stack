package org.sevensource.support.test.model.mock;

import java.util.Arrays;
import java.util.List;

import org.sevensource.support.test.jpa.model.mock.AbstractMockProvider;
import org.sevensource.support.test.jpa.model.mock.MockFactory;
import org.sevensource.support.test.model.UUIDTestEntity;
import org.sevensource.support.test.model.UUIDTestReferenceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UUIDTestEntityMockProvider extends AbstractMockProvider<UUIDTestEntity> {
	
	@Autowired
	MockFactory<?> mockFactory;
	
	private final static String NAME = "MOCK";
	private int seed = 0;
	
	public UUIDTestEntityMockProvider() {
		super(UUIDTestEntity.class);
	}
	
	@Override
	public UUIDTestEntity populate() {
		UUIDTestEntity e = getRandomizer().nextObject(UUIDTestEntity.class);
		e.setTitle(NAME + seed++);
		UUIDTestReferenceEntity ref = mockFactory.on(UUIDTestReferenceEntity.class).create();
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
