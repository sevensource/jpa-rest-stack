package org.sevensource.support.test.domain.mock;

import java.util.Arrays;
import java.util.List;

import org.sevensource.support.test.domain.UUIDTestEntity;
import org.sevensource.support.test.domain.UUIDTestReferenceEntity;
import org.sevensource.support.test.jpa.domain.mock.AbstractMockProvider;
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
		UUIDTestReferenceEntity ref = create(UUIDTestReferenceEntity.class);
		
		UUIDTestEntity e = getRandomizer().nextObject(UUIDTestEntity.class);
		e.setTitle(NAME + seed++);
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
