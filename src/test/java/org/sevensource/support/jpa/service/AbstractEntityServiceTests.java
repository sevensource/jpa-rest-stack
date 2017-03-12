package org.sevensource.support.jpa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.junit.Test;
import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.jpa.model.PersistentEntity;
import org.sevensource.support.jpa.model.mock.MockFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.tngtech.java.junit.dataprovider.DataProvider;


@DataJpaTest
public abstract class AbstractEntityServiceTests<T extends PersistentEntity<UUID>> {
	
	@Autowired
	TestEntityManager tem;
	
	@Autowired
	EntityManagerFactory emf;
	
	@Autowired
	private EntityService<T, UUID> service;
	
	
	private final Class<T> domainClass;
	
	public AbstractEntityServiceTests(Class<T> domainClass) {
		this.domainClass = domainClass;
	}
	
	protected EntityService<T, UUID> getService() {
		return service;
	}
	
	protected T populate() {
		return MockFactory.on(domainClass).populate();
	}
	
	protected T createEntity() {
		return MockFactory.on(domainClass).create();
	}
	
	protected List<T> createEntity(int counter) {
		List<T> list = new ArrayList<>();
		for(int i=0; i<counter; i++) {
			list.add(MockFactory.on(domainClass).create());
		}
		return list;
	}
	
	protected T touch(T e) {
		return MockFactory.on(domainClass).touch(e);
	}
	
	@BeforeTransaction
	public void beforeTransaction() {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		for(Class<?> clazz : getEntityClassesForDeletion()) {
			CriteriaBuilder criteriaBuilder  = em.getCriteriaBuilder();
			CriteriaDelete query = criteriaBuilder.createCriteriaDelete(clazz);
			Root<?> root = query.from(clazz);
			int result = em.createQuery(query).executeUpdate();
		}
		
		for(T e : getEntitiesForBusinessValidation()) {
			em.persist(e);
		}
		tx.commit();
	}
	
	protected abstract List<T> getEntitiesForBusinessValidation(); 
	protected abstract List<Class<?>> getEntityClassesForDeletion();
	
	// ABSTRACTS
	@Test(expected=EntityValidationException.class)
	public abstract void create_with_business_violation();
	@Test(expected=EntityValidationException.class)
	public abstract void create_withId_with_business_violation();
	@Test(expected=EntityValidationException.class)
	public abstract void update_with_business_violation();
	
	
	///// GET
	@Test(expected=IllegalArgumentException.class)
	public void get_with_null_argument() {
		getService().get(null);
	}
	
	@Test()
	public void get_with_nonexistant_id() {
		createEntity();
		T e = getService().get(UUID.randomUUID());
		assertThat(e).isNull();
	}
	
	@Test()
	public void get_with_existing_id() {
		UUID id = createEntity().getId();
		T e = getService().get(id);
		assertThat(id).isEqualTo(e.getId());
	}
	
	//// EXISTS
	@Test(expected=IllegalArgumentException.class)
	public void exists_with_null_argument() {
		getService().exists(null);
	}
	
	@Test
	public void exists_with_nonexistant_id() {
		createEntity();
		boolean r = getService().exists(UUID.randomUUID());
		assertThat(r).isFalse();
	}
	
	@Test
	public void exists_with_existing_id() {
		UUID id = createEntity().getId();
		boolean r = getService().exists(id);
		assertThat(r).isTrue();
	}
	
	//// CREATE
	@Test(expected=IllegalArgumentException.class)
	public void create_with_null_argument() {
		getService().create(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void create_with_populated_id() {
		T e = populate();
		e.setId(UUID.randomUUID());
		getService().create(e);
	}
	
	@Test
	public void create_persists_an_entity() {
		T e = populate();
		e = getService().create(e);
		tem.flush();
		assertThat(e.getId()).isNotNull();
		T e1 = tem.find(domainClass, e.getId());
		assertThat(e1.getId()).isNotNull();
		assertThat(e).isEqualTo(e1);
	}
	
	@Test()
	public void version_is_incremented() {
		T e = populate();
		e = getService().create(e);
		tem.flush();
		
		assertThat(e.getVersion()).isEqualTo(0);
		
		touch(e);
		e = getService().update(e.getId(), e);
		tem.flush();
		
		assertThat(e.getVersion()).isEqualTo(1);
	}
	
	@Test()
	public void entity_has_auditing_data_after_create() {
		T e = populate();
		e = getService().create(e);
		tem.flush();
		
		assertThat(e.getCreatedBy()).isNotBlank();
		assertThat(e.getLastModifiedBy()).isNotBlank();
		assertThat(e.getCreatedDate()).isNotNull();
		assertThat(e.getLastModifiedDate()).isNotNull();
	}
	
	// CREATE WITH ID
	@Test
	public void create_by_id_with_first_null_argument() {
		T e = getService().create(null, populate());
		tem.flush();
		
		assertThat(e.getId()).isNotNull();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void create_by_id_with_second_null_argument() {
		getService().create(UUID.randomUUID(), null);
	}
	
	@Test
	public void create_by_id() {
		T e = populate();
		UUID id = UUID.randomUUID();
		e = getService().create(id, e);
		tem.flush();
		
		assertThat(e.getId()).isEqualTo(id);
	}
	
	@Test(expected=EntityAlreadyExistsException.class)
	public void create_by_id_with_existing_id() {
		UUID id = createEntity().getId();
		T e = populate();
		e.setId(id);
		e = getService().create(id, e);
	}
	
	//// UPDATE
	@Test(expected=IllegalArgumentException.class)
	public void update_with_null_entity_argument() {
		getService().update(UUID.randomUUID(), null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void update_with_null_id_argument() {
		getService().update(null, populate());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void update_with_differing_ids() {
		T e = populate();
		e.setId(UUID.randomUUID());
		getService().update(UUID.randomUUID(), e);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void update_nonexisting_id() {
		createEntity();
		T e = populate();
		e.setId(UUID.randomUUID());
		getService().update(e.getId(), e);
	}
	
	@Test
	public void update_works_for_new_entity() {
		T e = populate();
		e = getService().create(e);
		tem.flush();
		
		UUID id = e.getId();
		touch(e);
		e = getService().update(e.getId(), e);
		tem.flush();
		
		assertThat(e.getId()).isEqualTo(id);
		assertThat(e.getVersion()).isEqualTo(1);
		assertThat(e.getCreatedDate()).isLessThan(e.getLastModifiedDate());
	}
	
	@Test
	public void update_works_for_existing_entity() {
		T e = createEntity();
		UUID id = e.getId();
		touch(e);
		e = getService().update(e.getId(), e);
		tem.flush();
		
		assertThat(e.getId()).isEqualTo(id);
		assertThat(e.getVersion()).isEqualTo(1);
		assertThat(e.getCreatedDate()).isLessThan(e.getLastModifiedDate());
	}
	
	
	/// DELETE
	@Test(expected=IllegalArgumentException.class)
	public void delete_with_null_argument() {
		getService().delete(null);
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void delete_nonexisting_id() {
		getService().delete(UUID.randomUUID());
	}
	
	@Test
	public void delete_existing_id() {
		UUID id = createEntity().getId();
		getService().delete(id);
		tem.flush();
		
		T e = tem.find(domainClass, id);
		assertThat(e).isNull();
	}
	
	@Test
	public void create_delete_exists() {
		UUID id = createEntity().getId();
		getService().delete(id);
		assertThat(getService().exists(id)).isFalse();
	}
	
	
	// FIND (PAGEABLE)
	@Test
	@DataProvider({ "0", "10", "20", "30"})
	public void findall_is_correct_with_pageable(int count) {
		
		createEntity(count);
		tem.flush();
		tem.clear();
		count = getEntityCount();
		
		int pagesize = 7;
		for(int c=0; c<3;c++) {
			int expected;
			if(count == 0) {
				expected = 0;
			} else if(count >= pagesize * (c + 1)) {
				expected = pagesize;
			} else {
				expected = Math.max(count - pagesize * c, 0);
			}

			PageRequest pageRequest = new PageRequest(c, pagesize);
			Page<T> res = getService().findAll(pageRequest);
			assertThat(res).size().isEqualTo(expected);
		}
	}
	
	// FIND (Sort)
	@Test
	@DataProvider({ "0", "10", "20", "30"})
	public void findAllIsCorrectWithSort(int count) {
		
		createEntity(count);
		tem.flush();
		tem.clear();
		count = getEntityCount();
		
		Sort sort = new Sort("id");
		List<T> res = getService().findAll(sort);
		assertThat(res).size().isEqualTo(count);
	}
	
	private int getEntityCount() {
		CriteriaBuilder cb = tem.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(domainClass);
		Root<T> root = cq.from(domainClass);
		cq = cq.where();
        TypedQuery<T> q = tem.getEntityManager().createQuery(cq);
        List<T> results = q.getResultList();
        return results.size();
	}
	
}
