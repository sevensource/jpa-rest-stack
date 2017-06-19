package org.sevensource.support.jpa.liquibase.diff;

import java.util.Arrays;

import org.junit.Test;
import org.sevensource.support.jpa.configuration.JpaTestConfiguration;
import org.sevensource.support.jpa.liquibase.MultiTenantLiquibaseRunner;
import org.springframework.context.annotation.Import;

public class LiquibaseDiffRunnerSupportTest {

	public static class MyLiquibaseRunner extends MultiTenantLiquibaseRunner {
		public MyLiquibaseRunner() {
			super(null, "liquibase/test/liquibase-test.xml", "public");
		}
	}
	
	@Import({ JpaTestConfiguration.class})
	public static class TestLiquibaseDiffRunner extends LiquibaseDiffRunnerSupport {
		
		public static void main(String[] args) {
			startDatabaseDiff(TestLiquibaseDiffRunner.class, Arrays.asList(MyLiquibaseRunner.class), "public");
		}
	}
	
	@Test
	public void works() {
		TestLiquibaseDiffRunner.main(new String[]{});
	}
}
