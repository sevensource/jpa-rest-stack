package org.sevensource.support.test.jpa.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.sevensource.support.jpa.model.PersistentEntity;

public class JpaEqualityAndHashCodeVerifier<E extends PersistentEntity<?>> {

	private E entity;
	private Class<E> domainClass;
	private EntityManagerFactory entityManagerFactory;
	
	public JpaEqualityAndHashCodeVerifier(E entity, EntityManagerFactory entityManagerFactory) {
		this.entity = entity;
		this.domainClass = (Class<E>) this.entity.getClass();
		this.entityManagerFactory = entityManagerFactory;
	}
	
	public void verify() {
		
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
			entityManager = entityManagerFactory.createEntityManager();
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
