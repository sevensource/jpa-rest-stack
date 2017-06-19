package org.sevensource.support.jpa.liquibase;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JdbcTest
public class MultiTenantLiquibaseRunnerTest {

	@Configuration
	static class DummyConfig {}
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Test
	public void multiTenantLiquibaseRunner_works() throws Exception {
		MultiTenantLiquibaseRunner runner = new MultiTenantLiquibaseRunner(
				jdbcTemplate.getDataSource(),
				"liquibase/test/liquibase-test.xml", "public");
		runner.update();
		
		jdbcTemplate.execute("SELECT * FROM TEST");
	}
	
	@Test
	public void multiTenantLiquibaseRunner_works_with_schemas() throws Exception {
		MultiTenantLiquibaseRunner runner = new MultiTenantLiquibaseRunner(
				jdbcTemplate.getDataSource(),
				"liquibase/test/liquibase-test.xml", "public", Arrays.asList("schema_a", "schema_b"));
		runner.update();
		
		jdbcTemplate.execute("SELECT * FROM public.TEST");
		jdbcTemplate.execute("SELECT * FROM schema_a.TEST");
		jdbcTemplate.execute("SELECT * FROM schema_b.TEST");
	}
}
