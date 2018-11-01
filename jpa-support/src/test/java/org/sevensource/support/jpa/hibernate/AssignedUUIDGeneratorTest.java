package org.sevensource.support.jpa.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.sevensource.support.jpa.domain.UUIDEntity;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaTestConfiguration.class)
@Import({ValidationAutoConfiguration.class})
public class AssignedUUIDGeneratorTest {

	@PersistenceContext
	EntityManager em;

	@Test
	public void generator_creates_id_with_null_id() {
		UUIDEntity e = new UUIDEntity();
		e.setId(null);
		em.persist(e);
		em.flush();

		assertThat(e.getId()).isNotNull();
		em.clear();
		e = em.find(UUIDEntity.class, e.getId());
		assertThat(e).isNotNull();
		assertThat(e.getId()).isNotNull();
	}

	@Test
	public void generator_uses_existing_id() {
		UUIDEntity e = new UUIDEntity();
		UUID id = UUID.randomUUID();
		e.setId(id);
		em.persist(e);
		em.flush();

		assertThat(e.getId()).isEqualTo(id);
		em.clear();
		e = em.find(UUIDEntity.class, e.getId());
		assertThat(e).isNotNull();
		assertThat(e.getId()).isEqualTo(id);
	}

}
