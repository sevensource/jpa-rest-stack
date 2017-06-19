package org.sevensource.support.test.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.test.jpa.configuration.JpaSupportTestConfiguration;
import org.sevensource.support.test.jpa.domain.UUIDTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {JpaSupportTestConfiguration.class})
@EntityScan(basePackageClasses=UUIDTestEntity.class)
public class DataSourceAssertionsTest {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@PersistenceContext
	EntityManager em;
	
	@Before
	public void before() {
		DataSourceAssertions.reset();
	}
	
	@Test
	public void datasourceAssertion_select_works() {
		
		jdbcTemplate.execute("SELECT 1");
		assertThat(DataSourceAssertions.selectCount()).isEqualTo(1);
	}
	
	@Test
	public void datasourceAssertion_works() {
		UUIDTestEntity e = new UUIDTestEntity();
		e.setTitle(UUID.randomUUID().toString());
		em.persist(e);
		em.flush();
		assertThat(DataSourceAssertions.insertCount()).isEqualTo(1);
		assertThat(DataSourceAssertions.selectCount()).isEqualTo(0);
		
		em.clear();
		e = em.find(UUIDTestEntity.class, e.getId());
		assertThat(DataSourceAssertions.selectCount()).isEqualTo(1);
		
		e.setTitle(UUID.randomUUID().toString());
		e = em.merge(e);
		em.flush();
		assertThat(DataSourceAssertions.updateCount()).isEqualTo(1);
		
		em.remove(e);
		em.flush();
		assertThat(DataSourceAssertions.deleteCount()).isEqualTo(1);
	}
}
