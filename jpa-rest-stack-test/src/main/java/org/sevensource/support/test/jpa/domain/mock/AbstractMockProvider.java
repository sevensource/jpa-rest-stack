package org.sevensource.support.test.jpa.domain.mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.sevensource.support.jpa.domain.AbstractIntegerEntity;
import org.sevensource.support.jpa.domain.AbstractPersistentEntity;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;


public abstract class AbstractMockProvider<T extends PersistentEntity<?>> implements MockProvider<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMockProvider.class);

	private final Class<T> domainClass;

	private EnhancedRandom random;

	@Autowired(required=false)
	private TestEntityManager tem;

	@Autowired(required=false)
	private EntityManagerFactory emf;

	@Autowired
	MockFactory mockFactory;


	public AbstractMockProvider(Class<T> domainClass) {
		this.domainClass = domainClass;
	}

	public void setMockFactory(MockFactory mockFactory) {
		this.mockFactory = mockFactory;
	}

	@PostConstruct
	public void postConstruct() {
		EnhancedRandomBuilder randomBuilder = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
				   .seed(123L)
				   .objectPoolSize(100)
				   .randomizationDepth(3)
				   .stringLengthRange(5, 50)
				   .collectionSizeRange(1, 10)
				   .scanClasspathForConcreteTypes(true)
				   .overrideDefaultInitialization(true);

		if(tem != null || emf != null) {
			randomBuilder = randomBuilder
					.exclude(FieldDefinitionBuilder.field().named("id").inClass(AbstractUUIDEntity.class).get())
					.exclude(FieldDefinitionBuilder.field().named("_id").inClass(AbstractUUIDEntity.class).get())
					.exclude(FieldDefinitionBuilder.field().named("id").inClass(AbstractIntegerEntity.class).get())
					.exclude(FieldDefinitionBuilder.field().named("lastModifiedBy").inClass(AbstractPersistentEntity.class).get())
					.exclude(FieldDefinitionBuilder.field().named("createdBy").inClass(AbstractPersistentEntity.class).get())
					.exclude(FieldDefinitionBuilder.field().named("lastModifiedDate").ofType(Instant.class).inClass(AbstractPersistentEntity.class).get())
					.exclude(FieldDefinitionBuilder.field().named("createdDate").ofType(Instant.class).inClass(AbstractPersistentEntity.class).get())
					.exclude(FieldDefinitionBuilder.field().named("version").ofType(Integer.class).inClass(AbstractPersistentEntity.class).get());
		}

		random = randomBuilder.build();
	}

	@Override
	public Class<T> getDomainClass() {
		return domainClass;
	}

	@Override
	public List<T> create(int count) {
		List<T> list = new ArrayList<>();
		for(int i=0; i<count; i++) {
			list.add(create());
		}

		return list;
	}

	@Override
	public T create() {
		T mock = populate();
		if(hasExistingEntityManagerTransaction()) {
			mock = persistWithExistingTransaction(mock);
		} else if(emf != null) {
			mock = persistWithNewTransaction(mock);
		} else {
			logger.warn("Mocking persist() of entity {}", mock.getClass().getSimpleName());
			setId(mock);
		}

		return mock;
	}

	private boolean hasExistingEntityManagerTransaction() {
		if(tem == null) {
			return false;
		}
		try {
			tem.getEntityManager();
			return true;
		} catch(IllegalStateException e) {
			return false;
		}
	}

	private T persistWithExistingTransaction(T mock) {
		tem.persist(mock);
		tem.flush();
		return tem.find(domainClass, mock.getId());
	}

	private T persistWithNewTransaction(T mock) {
		EntityManager entityManager = null;
		EntityTransaction txn = null;
		try {
			entityManager = emf.createEntityManager();
			txn = entityManager.getTransaction();
			txn.begin();

			entityManager.persist(mock);
			entityManager.flush();
			entityManager.refresh(mock);

			txn.commit();
			return mock;
		} catch (Exception e) {
			if (txn != null && txn.isActive()) {
				txn.rollback();
			}
			throw new IllegalArgumentException(e);
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
	}

	/**
	 * set an ID on the mock manually
	 * @param mock
	 */
	protected void setId(T mock) {
		if(mock instanceof AbstractUUIDEntity) {
			((AbstractUUIDEntity)mock).setId(UUID.randomUUID());
		} else if(mock instanceof AbstractIntegerEntity) {
			((AbstractIntegerEntity)mock).setId(random.nextInt());
		} else {
			throw new IllegalStateException("Override setId() - don't know how to set id on " + mock.getClass().getSimpleName());
		}
	}


	protected <E extends PersistentEntity<?>> E create(Class<E> clazz) {
		return mockFactory.on(clazz).create();
	}

//	protected <E extends PersistentEntity<?>> E populate(Class<E> clazz) {
//		return mockFactory.on(clazz).populate();
//	}

	protected EnhancedRandom getRandomizer() {
		return random;
	}
}
