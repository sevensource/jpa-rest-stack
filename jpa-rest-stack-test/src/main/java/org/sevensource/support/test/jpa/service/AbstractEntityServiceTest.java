package org.sevensource.support.test.jpa.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Test;
import org.sevensource.support.jpa.domain.PersistentEntity;
import org.sevensource.support.jpa.exception.EntityAlreadyExistsException;
import org.sevensource.support.jpa.exception.EntityNotFoundException;
import org.sevensource.support.jpa.exception.EntityValidationException;
import org.sevensource.support.jpa.service.EntityService;
import org.sevensource.support.test.jpa.AbstractJpaTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.tngtech.java.junit.dataprovider.DataProvider;


@DataJpaTest
public abstract class AbstractEntityServiceTest<T extends PersistentEntity<UUID>> extends AbstractJpaTestSupport<T> {

	@Autowired
	private EntityService<T, UUID> service;

	private final Class<T> domainClass;


	public AbstractEntityServiceTest(Class<T> domainClass) {
		super(domainClass);
		this.domainClass = domainClass;
	}

	protected EntityService<T, UUID> getService() {
		return service;
	}

	@BeforeTransaction
	public void beforeTransaction() {
		deleteAll();

		List<T> entities = getEntitesToPersistBeforeTransaction();

		EntityManager em = getEntityManagerFactory().createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		if(entities != null) {
			for(T e : entities) {
				em.persist(e);
			}
		}
		em.flush();
		tx.commit();
		em.clear();
		em.close();
	}

	/**
	 * Persist entities before transaction to perform tests, that violate, ie. database constraints.
	 *
	 * @return a list of entities to persist before each transaction, can be null
	 */
	protected abstract List<T> getEntitesToPersistBeforeTransaction();

	/**
	 * get a list of entities with invalid data, which are expected to cause an {@link EntityValidationException}
	 * @return
	 */
	protected abstract List<T> getEntitiesWithValidationViolations();

	/*** VALIDATION TESTS ***/
	/************************/
	@Test
	public void create_with_validation_violation() {
		List<T> entities = getEntitiesWithValidationViolations();
		if(entities != null) {
			for(T e : entities) {
				try {
					getService().create(e);
					fail("Create should fail with invalid entity: " + e);
				} catch(EntityValidationException ex) {
					// ok, as expected
				}
			}
		}
	}

	@Test
	public void create_withId_with_validation_violation() {
		List<T> entities = getEntitiesWithValidationViolations();
		if(entities != null) {
			for(T e : entities) {
				try {
					getService().create(UUID.randomUUID(), e);
					fail("Create should fail with invalid entity: " + e);
				} catch(EntityValidationException ex) {
					// ok, as expected
				}
			}
		}
	}

	@Test
	public void update_with_validation_violation() {
		List<T> entities = getEntitiesWithValidationViolations();
		if(entities != null) {
			List<T> created = createEntity(entities.size());
			getEntityManager().flush();

			for(int i=0; i<created.size(); i++) {
				try {
					T e = entities.get(i);
					getService().get(created.get(i).getId());
					UUID id = created.get(i).getId();
					e.setId(id);
					getService().update(id, e);
					fail("Update should fail with invalid entity: " + e);
				} catch(EntityValidationException ex) {
					// ok, as expected
				}
			}
		}
	}


	/***     GET TESTS    ***/
	/************************/
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


	/***   EXISTS TESTS   ***/
	/************************/
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


	/***   CREATE TESTS   ***/
	/************************/
	@Test(expected=IllegalArgumentException.class)
	public void create_with_null_argument() {
		getService().create(null);
	}

	@Test()
	public void create_with_populated_id() {
		T e = populate();
		e.setId(UUID.randomUUID());
		getService().create(e);
	}

	@Test
	public void create_persists_an_entity() {
		T e = populate();
		e = getService().create(e);
		getEntityManager().flush();
		assertThat(e.getId()).isNotNull();
		T e1 = getEntityManager().find(domainClass, e.getId());
		assertThat(e1.getId()).isNotNull();
		assertThat(e).isEqualTo(e1);
	}

	@Test()
	public void version_is_incremented() {
		T e = populate();
		e = getService().create(e);
		getEntityManager().flush();

		assertThat(e.getVersion()).isEqualTo(0);

		touch(e);
		e = getService().update(e.getId(), e);
		getEntityManager().flush();

		assertThat(e.getVersion()).isEqualTo(1);
	}

	@Test()
	public void entity_has_auditing_data_after_create() {
		T e = populate();
		e = getService().create(e);
		getEntityManager().flush();

		assertThat(e.getCreatedBy()).isNotBlank();
		assertThat(e.getLastModifiedBy()).isNotBlank();
		assertThat(e.getCreatedDate()).isNotNull();
		assertThat(e.getLastModifiedDate()).isNotNull();
	}

	/*** CREATE w/ID TESTS ***/
	/*************************/
	@Test
	public void create_by_id_with_first_null_argument() {
		T e = getService().create(null, populate());
		getEntityManager().flush();

		assertThat(e.getId()).isNotNull();
	}

	@Test(expected=IllegalArgumentException.class)
	public void create_by_id_with_second_null_argument() {
		getService().create(UUID.randomUUID(), null);
	}

	@Test
	public void create_by_id() {
		UUID id = UUID.randomUUID();
		T e = populate();
		e.setId(id);
		e = getService().create(id, e);
		getEntityManager().flush();

		assertThat(e.getId()).isEqualTo(id);
	}

	@Test(expected=EntityValidationException.class)
	public void create_by_id_with_different_ids() {
		T e = populate();
		UUID id = UUID.randomUUID();
		getService().create(id, e);
		getEntityManager().flush();
	}

	@Test(expected=EntityAlreadyExistsException.class)
	public void create_by_id_with_existing_id() {
		UUID id = createEntity().getId();
		T e = populate();
		e.setId(id);
		getService().create(id, e);
	}


	/***   UPDATE TESTS   ***/
	/************************/
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
		getEntityManager().flush();

		UUID id = e.getId();
		touch(e);
		e = getService().update(e.getId(), e);
		getEntityManager().flush();

		assertThat(e.getId()).isEqualTo(id);
		assertThat(e.getVersion()).isEqualTo(1);
		assertThat(e.getCreatedDate()).isBefore(e.getLastModifiedDate());
	}

	@Test
	public void update_works_for_existing_entity() {
		T e = createEntity();
		UUID id = e.getId();
		touch(e);
		e = getService().update(e.getId(), e);
		getEntityManager().flush();

		assertThat(e.getId()).isEqualTo(id);
		assertThat(e.getVersion()).isEqualTo(1);
		assertThat(e.getCreatedDate()).isBefore(e.getLastModifiedDate());
	}


	/***   DELETE TESTS   ***/
	/************************/
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
		getEntityManager().flush();

		T e = getEntityManager().find(domainClass, id);
		assertThat(e).isNull();
	}

	@Test
	public void create_delete_exists() {
		UUID id = createEntity().getId();
		getService().delete(id);
		assertThat(getService().exists(id)).isFalse();
	}


	/***    FIND TESTS    ***/
	/************************/
	@Test
	@DataProvider({ "0", "10", "20", "30"})
	public void findall_is_correct_with_pageable(int count) {

		createEntity(count);
		getEntityManager().flush();
		getEntityManager().clear();
		int entityCount = getEntityCount();

		int pagesize = 7;
		for(int c=0; c<3;c++) {
			int expected;
			if(entityCount == 0) {
				expected = 0;
			} else if(entityCount >= pagesize * (c + 1)) {
				expected = pagesize;
			} else {
				expected = Math.max(entityCount - pagesize * c, 0);
			}

			PageRequest pageRequest = PageRequest.of(c, pagesize);
			Page<T> res = getService().findAll(pageRequest);
			assertThat(res).size().isEqualTo(expected);
		}
	}

	@Test
	@DataProvider({ "0", "10", "20", "30"})
	public void findall_is_correct_with_sort(int count) {

		createEntity(count);
		getEntityManager().flush();
		getEntityManager().clear();
		int createdCount = getEntityCount();


		Sort sort = Sort.by("id");
		List<T> res = getService().findAll(sort);
		assertThat(res).size().isEqualTo(createdCount);
	}
}
