package org.sevensource.support.jpa.liquibase.diff;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sevensource.support.jpa.liquibase.MultiTenantLiquibaseRunner;
import org.sevensource.support.jpa.liquibase.diff.LiquibaseDiffGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JdbcTest
public class LiquibaseDiffGeneratorTest {
	@Configuration
	static class DummyConfig {}
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public static class MyLiquibaseRunner extends MultiTenantLiquibaseRunner {
		public MyLiquibaseRunner() {
			super(null, "liquibase/test/liquibase-test.xml", "public");
		}
	}
	
	@Test
	public void liquibaseDiffGenerator_with_no_differences_works() throws Exception {
		MultiTenantLiquibaseRunner runner = new MyLiquibaseRunner();
		runner.setDataSource(jdbcTemplate.getDataSource());
		runner.update();
		
		LiquibaseDiffGenerator diffGenerator = new LiquibaseDiffGenerator();
		diffGenerator.setSourceDataSource(jdbcTemplate.getDataSource());
		diffGenerator.setLiquibaseRunner(Arrays.asList(MyLiquibaseRunner.class));
		diffGenerator.setSchemaToDiff("public");
		diffGenerator.run();
	}
	
	@Test
	public void liquibaseDiffGenerator_with_differences_works() throws Exception {
		MultiTenantLiquibaseRunner runner = new MyLiquibaseRunner();
		runner.setDataSource(jdbcTemplate.getDataSource());
		runner.update();
		
		LiquibaseDiffGenerator diffGenerator = new LiquibaseDiffGenerator();
		diffGenerator.setSourceDataSource(jdbcTemplate.getDataSource());
		diffGenerator.setLiquibaseRunner(Arrays.asList(MyLiquibaseRunner.class));
		diffGenerator.setSchemaToDiff("public");
		diffGenerator.run();
	}
}
