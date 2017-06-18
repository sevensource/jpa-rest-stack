package org.sevensource.support.test.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.test.jpa.domain.mock.MockFactory;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractJpaTestSupport<T extends PersistentEntity<?>> {

	@PersistenceContext
	EntityManager em;
	
	@Autowired
	MockFactory mockFactory;
	
	private final Class<T> domainClass;
	
	public AbstractJpaTestSupport(Class<T> domainClass) {
		this.domainClass = domainClass;
	}
	
	protected EntityManager getEntityManager() {
		return em;
	}
	
	protected EntityManagerFactory getEntityManagerFactory() {
		return getEntityManager().getEntityManagerFactory();
	}
	
	protected T populate() {
		return mockFactory.on(domainClass).populate();
	}
	
	protected T createEntity() {
		return mockFactory.on(domainClass).create();
	}
	
	protected List<T> createEntity(int counter) {
		return mockFactory.on(domainClass).create(counter);
	}
	
	protected T touch(T e) {
		return mockFactory.on(domainClass).touch(e);
	}
	
	protected List<Class<?>> getDeletionOrder() {
		return mockFactory.on(domainClass).getDeletionOrder();
	}
	
	protected void ensureEmpty() {
		int count = getEntityCount();
		assertThat(count)
			.withFailMessage("Expected count of %s to be 0, but it is %d", domainClass.getSimpleName(), count)
			.isEqualTo(0);
	}
	
	protected int getEntityCount() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> q = cb.createQuery(domainClass);
		q.from(domainClass);
		List<T> list = getEntityManager().createQuery(q).getResultList();
        return list.size();
	}
	
	protected void deleteAll() {
		
		List<Class<?>> deletionClasses = getDeletionOrder();
		
		EntityManager em = getEntityManagerFactory().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		for(Class<?> clazz : deletionClasses) {
			CriteriaBuilder criteriaBuilder  = em.getCriteriaBuilder();
			CriteriaQuery query = criteriaBuilder.createQuery(clazz);
			query.from(clazz);
			List<?> results = em.createQuery(query).getResultList();
			
			//Delete one-by-one to also delete @Embeddables and Cascades
			for(Object o : results) {
				em.remove(o);
			}
			em.flush();
		}
		
		tx.commit();
		em.clear();
		em.close();
	}
}
