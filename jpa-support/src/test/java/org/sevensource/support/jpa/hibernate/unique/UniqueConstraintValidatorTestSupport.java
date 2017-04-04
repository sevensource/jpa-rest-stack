package org.sevensource.support.jpa.hibernate.unique;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;
import org.sevensource.support.jpa.domain.AbstractUUIDEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.transaction.BeforeTransaction;

@DataJpaTest
public abstract class UniqueConstraintValidatorTestSupport<T extends AbstractUUIDEntity> {

	@PersistenceContext
	EntityManager em;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	Validator validator;
	
	abstract String getTableName();
	abstract String[] getColumns();
	abstract String[] getValues();
	abstract T populate(boolean fail);
	abstract void touchOne(T e, boolean fail);
	
	
	private UUID preinsertId = null;
	
	
	
	@BeforeTransaction
	public void beforeTransaction() {
		
		String table = getTableName();
		String columns = String.join(",  ", getColumns());
		String values = String.join(", ", getValues());
		
		preinsertId = UUID.randomUUID();
		
		jdbcTemplate.execute(String.format("DELETE FROM %s", getTableName()));
		jdbcTemplate.execute(String.format(
					"INSERT INTO %s " + 
					"(created_by, created_date, last_modified_by, last_modified_date, version, id, %s)" +
					"values " + 
					"('BEFORE_TRANSACTION', CURRENT_TIMESTAMP(), 'BEFORE_TRANSACTION', CURRENT_TIMESTAMP(), 0, %s, %s)",
					table, columns, "'"+ preinsertId.toString()+"'", values));
		
		
		int count = jdbcTemplate.queryForObject(
				String.format("SELECT count(*) FROM %s", getTableName()), new Object[] { }, Integer.class);
		
		assertThat(count).isEqualTo(1);
	}
	
	@Test
	public void insert_works() {
		T e = populate(false);
		Set<ConstraintViolation<T>> violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
		
		em.persist(e);
		em.flush();
		em.clear();
		assertThat(e.getId()).isNotNull();
		violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
		
	}
	
	@Test
	public void insert_fails() {
		T e = populate(true);
		Set<ConstraintViolation<T>> violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(1);
	}
	
	@Test
	public void merge_works() {
		T e = populate(false);
		Set<ConstraintViolation<T>> violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
		
		em.persist(e);
		em.flush();
		
		touchOne(e, false);
		violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
		
		assertThat(e.getId()).isNotNull();
		violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
	}
	
	@Test
	public void merge_fails() {
		T e = populate(false);
		Set<ConstraintViolation<T>> violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
		
		em.persist(e);
		em.flush();
		
		touchOne(e, true);
		violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(1);
	}
	
	@Test
	public void merge_works2() {
		T x = populate(false);
		T e = (T) em.find(x.getClass(), preinsertId);
		touchOne(e, false);
		
		Set<ConstraintViolation<T>> violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
		
		em.persist(e);
		em.flush();
		
		assertThat(e.getId()).isNotNull();
		violations = validator.validate(e, UniqueValidation.class);
		assertThat(violations).hasSize(0);
	}
}
