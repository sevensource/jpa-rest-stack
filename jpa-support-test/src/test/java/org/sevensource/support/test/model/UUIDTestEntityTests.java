package org.sevensource.support.test.model;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.test.jdbc.configuration.ProxyDatasourceConfiguration;
import org.sevensource.support.test.jpa.configuration.JpaAuditingTestConfiguration;
import org.sevensource.support.test.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.test.jpa.model.AbstractUUIDEntityTestSupport;
import org.sevensource.support.test.model.mock.UUIDTestEntityMockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JpaAuditingTestConfiguration.class, MockFactoryConfiguration.class, ProxyDatasourceConfiguration.class})
@ComponentScan(basePackageClasses={UUIDTestEntityMockProvider.class})
public class UUIDTestEntityTests extends AbstractUUIDEntityTestSupport<UUIDTestEntity> {

	@PersistenceContext
	EntityManager em;
	
	@Autowired
	DataSource ds;
	
	public UUIDTestEntityTests() {
		super(UUIDTestEntity.class);
	}
	
	@Test
	public void test_persist_with_reference() {
		
		UUIDTestEntity e = new UUIDTestEntity("Hello World 1");
		UUIDTestReferenceEntity r = new UUIDTestReferenceEntity();
		em.persist(r);
		e.setRef(r);
		em.persist(e);
		em.flush();
		
		
		em.detach(r);
		em.detach(e);
		e = em.find(e.getClass(), e.getId());
		assertThat(e.getId()).isNotNull();
		assertThat(e.getRef()).isNotNull();
		assertThat(e.getRef().getId()).isNotNull();
		assertThat(e.getRef()).isEqualTo(r);
	}
}
