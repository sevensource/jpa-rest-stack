package org.sevensource.support.test.jpa.model.mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;

import org.sevensource.support.jpa.model.AbstractPersistentEntity;
import org.sevensource.support.jpa.model.AbstractUUIDEntity;
import org.sevensource.support.jpa.model.PersistentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

public abstract class AbstractMockProvider<T extends PersistentEntity<?>> implements MockProvider<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMockProvider.class);

	private final static EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
		   .seed(123L)
		   .objectPoolSize(10)
		   .randomizationDepth(3)
		   .stringLengthRange(5, 50)
		   .collectionSizeRange(1, 10)
		   .scanClasspathForConcreteTypes(true)
		   .overrideDefaultInitialization(true)
		   .exclude(FieldDefinitionBuilder.field().named("id").inClass(AbstractUUIDEntity.class).get())
		   .exclude(FieldDefinitionBuilder.field().named("lastModifiedBy").inClass(AbstractPersistentEntity.class).get())
		   .exclude(FieldDefinitionBuilder.field().named("createdBy").inClass(AbstractPersistentEntity.class).get())
		   .exclude(FieldDefinitionBuilder.field().named("lastModifiedDate").ofType(Instant.class).inClass(AbstractPersistentEntity.class).get())
		   .exclude(FieldDefinitionBuilder.field().named("createdDate").ofType(Instant.class).inClass(AbstractPersistentEntity.class).get())
		   .exclude(FieldDefinitionBuilder.field().named("version").ofType(Integer.class).inClass(AbstractPersistentEntity.class).get())
		   .build();
	
	
	private final Class<T> domainClass;
	
	@Autowired(required=false)
	private TestEntityManager tem;
	
	@Autowired(required=false)
	private EntityManagerFactory emf;
	
	
	public AbstractMockProvider(Class<T> domainClass) {
		this.domainClass = domainClass;
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
		if(tem != null) {
			try {
				tem.persist(mock);
				tem.flush();
				mock = tem.find(domainClass, mock.getId());
				//mock = tem.persistFlushFind(mock);
			} catch (IllegalStateException | TransactionRequiredException iae) {
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
				} catch (Throwable e) {
					if (txn != null && txn.isActive())
						txn.rollback();
					throw e;
				} finally {
					if (entityManager != null) {
						entityManager.close();
					}
				}
			}
			
		} else {
			logger.warn("Mocking persist() of entity {}", mock.getClass().getSimpleName());
			setId(mock);
		}
		
		return mock;
	}
	
	/**
	 * set an ID on the mock manually
	 * @param mock
	 */
	protected void setId(T mock) {
		if(mock instanceof AbstractUUIDEntity) {
			((AbstractUUIDEntity)mock).setId(UUID.randomUUID());
		} else {
			throw new IllegalStateException("Override setId() - don't know how to set id on " + mock.getClass().getSimpleName());
		}
	}
	
	protected EnhancedRandom getRandomizer() {
		return random;
	}
}
