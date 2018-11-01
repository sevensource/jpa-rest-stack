package org.sevensource.support.test.jpa.domain.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.sevensource.support.test.jpa.configuration.JpaSupportTestConfiguration;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.sevensource.support.test.jpa.domain.UUIDTestReferenceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JpaSupportTestConfiguration.class})
@ComponentScan(basePackageClasses={UUIDTestEntityMockProvider.class})
@EntityScan(basePackageClasses=UUIDTestEntity.class)
public class UUIDTestEntityMockProviderTest {

	@PersistenceContext
	EntityManager em;

	@Autowired
	MockFactory mockFactory;

	@Test
	public void create_mock() {
		mockFactory.on(UUIDTestEntity.class).create();
	    TypedQuery<UUIDTestEntity> query = em.createQuery("SELECT e FROM UUIDTestEntity e", UUIDTestEntity.class);
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(1);
	}

	@Test
	public void create_many_mocks() {
		mockFactory.on(UUIDTestEntity.class).create(10);
		TypedQuery<UUIDTestEntity> query = em.createQuery("SELECT e FROM UUIDTestEntity e", UUIDTestEntity.class);
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(10);
	}

	@Test
	public void create_mock_check_reference() {
		mockFactory.on(UUIDTestEntity.class).create();
		TypedQuery<UUIDTestEntity> query = em.createQuery("SELECT e FROM UUIDTestEntity e", UUIDTestEntity.class);
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(1);

	    assertThat(list.get(0).getRef()).isNotNull();
	    assertThat(list.get(0).getRef().getId()).isNotNull();

	    TypedQuery<UUIDTestReferenceEntity> query1 = em.createQuery("SELECT e FROM UUIDTestReferenceEntity e", UUIDTestReferenceEntity.class);
	    List<UUIDTestReferenceEntity> list1 = query1.getResultList();
	    assertThat(list1).hasSize(1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void create_invalid_mock() {
		mockFactory.on(AbstractUUIDEntity.class);
	}


	@Test
	public void populate_mock() {
		UUIDTestEntity e = mockFactory.on(UUIDTestEntity.class).populate();
		assertThat(e).isNotNull();
	}

	@Test
	public void touch_mock() {
		UUIDTestEntity e = mockFactory.on(UUIDTestEntity.class).create();
		TypedQuery<UUIDTestEntity> query = em.createQuery("SELECT e FROM UUIDTestEntity e", UUIDTestEntity.class);
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(1);

	    String t = e.getTitle();
		assertThat(t).isNotNull();

	    mockFactory.on(UUIDTestEntity.class).touch(e);
	    em.merge(e);

	    assertThat(e.getTitle()).isNotNull();
	    assertThat(e.getTitle()).isNotEqualTo(t);
	}
}
