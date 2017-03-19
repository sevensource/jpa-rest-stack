package org.sevensource.support.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.sevensource.support.jpa.model.PersistentEntity;
import org.sevensource.support.jpa.model.mock.MockFactory;

public abstract class AbstractJpaTestSupport<T extends PersistentEntity<UUID>> {

	@PersistenceContext
	EntityManager em;
	
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
		return MockFactory.on(domainClass).populate();
	}
	
	protected T createEntity() {
		return MockFactory.on(domainClass).create();
	}
	
	protected List<T> createEntity(int counter) {
		return MockFactory.on(domainClass).create(counter);
	}
	
	protected T touch(T e) {
		return MockFactory.on(domainClass).touch(e);
	}
	
	protected void ensureEmpty() {
		assertThat(getEntityCount()).isEqualTo(0);
	}
	
	protected int getEntityCount() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> q = cb.createQuery(domainClass);
		q.from(domainClass);
		List<T> list = getEntityManager().createQuery(q).getResultList();
        return list.size();
	}
}
