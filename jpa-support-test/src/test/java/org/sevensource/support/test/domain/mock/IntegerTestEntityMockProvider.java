package org.sevensource.support.test.domain.mock;

import java.util.Arrays;
import java.util.List;

import org.sevensource.support.test.domain.IntegerTestEntity;
import org.sevensource.support.test.jpa.domain.mock.AbstractMockProvider;
import org.springframework.stereotype.Component;

@Component
public class IntegerTestEntityMockProvider extends AbstractMockProvider<IntegerTestEntity> {
	
	private final static String NAME = "MOCK";
	private int seed = 0;
	
	public IntegerTestEntityMockProvider() {
		super(IntegerTestEntity.class);
	}
	
	@Override
	public IntegerTestEntity populate() {
		IntegerTestEntity e = getRandomizer().nextObject(IntegerTestEntity.class);
		return e;
	}

	@Override
	public IntegerTestEntity touch(IntegerTestEntity entity) {
		entity.setName(NAME + seed++);
		return entity;
	}
	
	@Override
	public List<Class<?>> getDeletionOrder() {
		return Arrays.asList(IntegerTestEntity.class);
	}
}
