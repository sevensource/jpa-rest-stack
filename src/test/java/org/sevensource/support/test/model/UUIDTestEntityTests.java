package org.sevensource.support.test.model;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaAuditingTestConfiguration;
import org.sevensource.support.jpa.configuration.MockFactoryConfiguration;
import org.sevensource.support.jpa.model.AbstractPersistentEntity;
import org.sevensource.support.jpa.model.AbstractUUIDEntityTestSupport;
import org.sevensource.support.test.configuration.MockConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import nl.jqno.equalsverifier.EqualsVerifier;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JpaAuditingTestConfiguration.class, MockConfiguration.class})
public class UUIDTestEntityTests extends AbstractUUIDEntityTestSupport<UUIDTestEntity> {

	@PersistenceContext
	EntityManager em;
	
	public UUIDTestEntityTests() {
		super(UUIDTestEntity.class);
	}
	
	@Test
	public void equalsContract() {
	    EqualsVerifier
	    	.forClass(UUIDTestEntity.class)
	    	.withRedefinedSuperclass()
	    	.withNonnullFields("id")
	    	.withOnlyTheseFields("id")
        .verify();
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
		assertThat(e.getRef()).isEqualTo(r);
	}
}
