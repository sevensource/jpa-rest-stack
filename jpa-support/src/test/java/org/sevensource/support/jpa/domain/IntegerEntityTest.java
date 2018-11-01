package org.sevensource.support.jpa.domain;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = JpaTestConfiguration.class)
@Import({ValidationAutoConfiguration.class})
public class IntegerEntityTest {

	@PersistenceContext
	EntityManager em;

	@Test
	public void isNew_works() {
		IntegerEntity e = new IntegerEntity();
		assertThat(e.isNew()).isTrue();
		em.persist(e);
		em.flush();
		assertThat(e.isNew()).isFalse();
	}

	@Test
	public void equals_works() {
		IntegerEntity e = new IntegerEntity();
		IntegerEntity e1 = new IntegerEntity();
		assertThat(e1).isNotEqualTo(e);
		assertThat(e).isEqualTo(e);

		em.persist(e);
		assertThat(e).isEqualTo(e);
		em.flush();
		assertThat(e).isEqualTo(e);

		assertThat(e).isNotEqualTo(null);

		e1.setId(null);
		assertThat(e).isNotEqualTo(e1);

		e.setId(null);
		assertThat(e).isNotEqualTo(e1);

		assertThat(e).isNotEqualTo(new UUIDEntity());
	}

	@Test
	public void hashCode_works() {
		IntegerEntity e = new IntegerEntity();
		e.hashCode();
		em.persist(e);
		em.flush();
		e.hashCode();
	}

	@Test
	public void toString_works() {
		IntegerEntity e = new IntegerEntity();
		e.toString();
		em.persist(e);
		em.flush();
		e.toString();
	}

	@Test
	public void auditing_works() {
		IntegerEntity e = new IntegerEntity();
		e = em.merge(e);
		em.flush();

		assertThat(e.getCreatedBy()).isNotNull();
		assertThat(e.getCreatedDate()).isNotNull();
		assertThat(e.getLastModifiedBy()).isNotNull();
		assertThat(e.getLastModifiedDate()).isNotNull();
	}
}
