package org.sevensource.support.test.model.mock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.model.AbstractUUIDEntity;
import org.sevensource.support.test.jpa.configuration.JpaAuditingTestConfiguration;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.jpa.model.mock.MockFactory;
import org.sevensource.support.test.model.UUIDTestEntity;
import org.sevensource.support.test.model.UUIDTestReferenceEntity;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JpaAuditingTestConfiguration.class, MockFactoryConfiguration.class})
@ComponentScan(basePackageClasses={UUIDTestEntityMockProvider.class})
@EntityScan(basePackageClasses=UUIDTestEntity.class)
public class UUIDTestEntityMockProviderTests {

	@PersistenceContext
	EntityManager em;
	
	@Test
	public void create_mock() {
		MockFactory.on(UUIDTestEntity.class).create();
	    Query query = em.createQuery("SELECT e FROM UUIDTestEntity e");
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(1);
	}
	
	@Test
	public void create_many_mocks() {
		MockFactory.on(UUIDTestEntity.class).create(10);
	    Query query = em.createQuery("SELECT e FROM UUIDTestEntity e");
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(10);
	}
	
	@Test
	public void create_mock_check_reference() {
		MockFactory.on(UUIDTestEntity.class).create();
	    Query query = em.createQuery("SELECT e FROM UUIDTestEntity e");
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(1);
	    
	    assertThat(list.get(0).getRef()).isNotNull();
	    assertThat(list.get(0).getRef().getId()).isNotNull();
	    
	    Query query1 = em.createQuery("SELECT e FROM UUIDTestReferenceEntity e");
	    List<UUIDTestReferenceEntity> list1 = query1.getResultList();
	    assertThat(list1).hasSize(1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void create_invalid_mock() {
		MockFactory.on(AbstractUUIDEntity.class);
	}
	
	
	@Test
	public void populate_mock() {
		UUIDTestEntity e = MockFactory.on(UUIDTestEntity.class).populate();
		assertThat(e).isNotNull();
	}
	
	@Test
	public void touch_mock() {
		UUIDTestEntity e = MockFactory.on(UUIDTestEntity.class).create();
	    Query query = em.createQuery("SELECT e FROM UUIDTestEntity e");
	    List<UUIDTestEntity> list = query.getResultList();
	    assertThat(list).hasSize(1);
	    
	    String t = e.getTitle();
		assertThat(t).isNotNull();
	    
	    MockFactory.on(UUIDTestEntity.class).touch(e);
	    em.merge(e);
	    
	    assertThat(e.getTitle()).isNotNull();
	    assertThat(e.getTitle()).isNotEqualTo(t);
	}
}
