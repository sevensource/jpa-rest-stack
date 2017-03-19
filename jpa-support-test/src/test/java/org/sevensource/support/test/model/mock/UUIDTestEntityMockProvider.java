package org.sevensource.support.test.model.mock;

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
//		EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
//				   .seed(123L)
//				   .objectPoolSize(10)
//				   .randomizationDepth(3)
//				   .stringLengthRange(5, 50)
//				   .collectionSizeRange(1, 10)
//				   .scanClasspathForConcreteTypes(true)
//				   .overrideDefaultInitialization(true)
//				   .exclude(FieldDefinitionBuilder.field().named("id").get())
//				   .exclude(FieldDefinitionBuilder.field().named("lastModifiedBy").inClass(AbstractPersistentEntity.class).get())
//				   .exclude(FieldDefinitionBuilder.field().named("createdBy").inClass(AbstractPersistentEntity.class).get())
//				   .exclude(FieldDefinitionBuilder.field().named("lastModifiedDate").ofType(Instant.class).inClass(AbstractPersistentEntity.class).get())
//				   .exclude(FieldDefinitionBuilder.field().named("createdDate").ofType(Instant.class).inClass(AbstractPersistentEntity.class).get())
//				   .exclude(FieldDefinitionBuilder.field().named("version").ofType(Integer.class).inClass(AbstractPersistentEntity.class).get())
//				   .build();
//		   
//		UUIDTestReferenceEntity re = random.nextObject(UUIDTestReferenceEntity.class);
//		UUIDTestEntity re1 = random.nextObject(UUIDTestEntity.class);
		
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
