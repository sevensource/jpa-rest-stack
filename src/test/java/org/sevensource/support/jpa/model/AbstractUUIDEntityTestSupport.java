package org.sevensource.support.jpa.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.hibernate.Session;
import org.junit.Test;
import org.sevensource.support.jpa.configuration.JpaAuditingTestConfiguration;
import org.sevensource.support.jpa.model.mock.MockFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;


@DataJpaTest
public abstract class AbstractUUIDEntityTestSupport<E extends AbstractUUIDEntity> {

	@PersistenceContext
	EntityManager em;
	
	private final Class<E> domainClass;


	
	protected AbstractUUIDEntityTestSupport(Class<E> entityClazz) {
		this.domainClass = entityClazz;
	}
	
	protected E populateEntity() {
		return MockFactory.on(domainClass).populate();
	}
	
	protected EntityManager getEntityManager() {
		return em;
	}
	
	protected EntityManagerFactory getEntityManagerFactory() {
		return getEntityManager().getEntityManagerFactory();
	}
	
	private void ensureEmpty() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<E> q = cb.createQuery(domainClass);
		q.from(domainClass);
		
		List<E> list = getEntityManager().createQuery(q).getResultList();
		assertThat(list).isEmpty();
	}
	
	@Test
	public void equalsContract() {
	    EqualsVerifier
	    	.forClass(domainClass)
	    	.withRedefinedSuperclass()
	    	.suppress(Warning.STRICT_INHERITANCE)

	    	//.suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
	        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
	        .suppress(Warning.STRICT_HASHCODE)
	    	
        .verify();
	}
	
	@Test
	public void test_equality_with_empty_objects() {
		E entity1 = null;
		E entity2 = null;
		try {
			entity1 = domainClass.newInstance();
			entity2 = domainClass.newInstance();
		} catch (Exception e) {
			// do nothing, cannot instantiante without default constructor
			return;
		}
		
		Set<E> set = new HashSet<>();
		set.add(entity1);
		set.add(entity2);
		assertThat(set.size()).isEqualTo(1);
	}
	
	@Test
	public void test_equality_with_populated_objects() {
		E entity1 = populateEntity();
		E entity2 = populateEntity();
		
		Set<E> set = new HashSet<>();
		set.add(entity1);
		set.add(entity2);
		assertThat(set.size()).isEqualTo(2);
	}

	@Test
	public void persist_creates_a_UUID() {
		ensureEmpty();
		
		E e = populateEntity();
		getEntityManager().persist(e);
		getEntityManager().flush();
		assertThat(e.getId()).isNotNull();
	}

	@Test
	public void persist_updates_auditing() {
		ensureEmpty();
		
		E e = populateEntity();

		Instant BEGIN = Instant.now();
		try {
			Thread.sleep(101);
			getEntityManager().persist(e);
			getEntityManager().flush();
			Thread.sleep(101);
		} catch (InterruptedException e1) {
		}
		Instant END = Instant.now();

		assertThat(e.getCreatedBy()).isEqualTo(JpaAuditingTestConfiguration.AUDITOR_STRING);
		assertThat(e.getLastModifiedBy()).isEqualTo(JpaAuditingTestConfiguration.AUDITOR_STRING);

		assertThat(e.getCreatedDate()).isBetween(BEGIN, END);
		assertThat(e.getLastModifiedDate()).isBetween(BEGIN, END);

		assertThat(e.getVersion()).isEqualTo(0);
	}

	@Test
	public void persist_allows_existing_id() {
		ensureEmpty();
		
		UUID id = UUID.randomUUID();
		E e = populateEntity();
		e.setId(id);
		getEntityManager().persist(e);
		getEntityManager().flush();
		
		assertThat(e.getId()).isEqualTo(id);

		E ee = getEntityManager().find(domainClass, id);
		assertThat(ee.getId()).isEqualTo(id);
	}
	
	@Test
	public void equals_works() {
		ensureEmpty();
		
		E e1 = populateEntity();
		E e2 = populateEntity();
		
		assertThat(e1).isEqualTo(e1);
		assertThat(e2).isEqualTo(e2);
		assertThat(e1).isNotEqualTo(e2);

		getEntityManager().persist(e1);
		getEntityManager().flush();
		
		assertThat(e1).isEqualTo(e1);
		assertThat(e1).isNotEqualTo(e2);
		
		assertThat(e1).isNotEqualTo(null);
		assertThat(e1).isNotEqualTo(new Object());
	}

	@Test
	@Transactional(value=TxType.NOT_SUPPORTED)
	// We need NOT_SUPPORTED here to have full control over the transactions
	public void assert_equality_constraints() {

		ensureEmpty();
		E entity = populateEntity();
		
		
		Set<E> tuples = new HashSet<>();
		
		assertThat(tuples.contains(entity)).isFalse();
		tuples.add(entity);
		assertThat(tuples.contains(entity)).isTrue();

		doInJPA(entityManager -> {
			entityManager.persist(entity);
			entityManager.flush();
			assertThat(tuples.contains(entity)).as("The entity is found after it's persisted").isTrue();
		});

		assertThat(tuples.contains(entity)).as("The entity is found after the entity is detached").isTrue();

		doInJPA(entityManager -> {
			E _entity = entityManager.merge(entity);
			assertThat(tuples.contains(_entity)).as("The entity is found after it's merged").isTrue();
		});

		doInJPA(entityManager -> {
			entityManager.unwrap(Session.class).update(entity);
			assertThat(tuples.contains(entity)).as("The entity is found after it's reattached").isTrue();
		});

		doInJPA(entityManager -> {
			E _entity = entityManager.find(domainClass, entity.getId());
			assertThat(tuples.contains(_entity))
					.as("The entity is found after it's loaded in an other Persistence Context").isTrue();
		});

		executeSync(() -> {
			doInJPA(entityManager -> {
				E _entity = entityManager.find(domainClass, entity.getId());
				assertThat(tuples.contains(_entity)).as("The entity is found after it's loaded "
						+ "in an other Persistence Context and " + "in an other thread").isTrue();
			});
		});
		
		// now: delete - delete one by one to respect cascades
		doInJPA(entityManager -> {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<E> q = cb.createQuery(domainClass);
			q.from(domainClass);
			List<E> res = entityManager.createQuery(q).getResultList();
			for(E e : res) {
				entityManager.remove(e);
			}
		});
	}

	
	@FunctionalInterface
	protected interface JPATransactionVoidFunction extends Consumer<EntityManager> {
		default void beforeTransactionCompletion() {
		}

		default void afterTransactionCompletion() {
		}
	}
	
	protected void doInJPA(JPATransactionVoidFunction function) {
		EntityManager entityManager = null;
		EntityTransaction txn = null;
		try {
			entityManager = getEntityManagerFactory().createEntityManager();
			function.beforeTransactionCompletion();
			txn = entityManager.getTransaction();
			txn.begin();
			function.accept(entityManager);
			txn.commit();
		} catch (Throwable e) {
			if (txn != null && txn.isActive())
				txn.rollback();
			throw e;
		} finally {
			function.afterTransactionCompletion();
			if (entityManager != null) {
				entityManager.close();
			}
		}
	}
	
	@FunctionalInterface
	protected interface VoidCallable extends Callable<Void> {

		void execute();

		default Void call() throws Exception {
			execute();
			return null;
		}
	}

	protected void executeSync(VoidCallable callable) {
		executeSync(Collections.singleton(callable));
	}

	protected void executeSync(Collection<VoidCallable> callables) {
		try {
			List<Future<Void>> futures = executorService.invokeAll(callables);
			for (Future<Void> future : futures) {
				future.get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	protected final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
		Thread bob = new Thread(r);
		bob.setName("Bob");
		return bob;
	});
}
