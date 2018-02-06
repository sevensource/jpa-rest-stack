package org.sevensource.support.jpa.liquibase.diff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.sevensource.support.jpa.liquibase.LiquibaseRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration
public abstract class LiquibaseDiffRunnerSupport implements CommandLineRunner {

	public static final String LIQUIBASE_PROFILE = "liquibase_diff";

	@Autowired
	private DataSource primaryDataSource;

	private static List<Class<? extends LiquibaseRunner>> liquibaseRunner;
	private static String schemaToDiff;


	protected static void startDatabaseDiff(Class<? extends LiquibaseDiffRunnerSupport> source, List<Class<? extends LiquibaseRunner>> list, String schema) {

		liquibaseRunner = list;
		schemaToDiff = schema;

		Map<String, Object> props = new HashMap<>();
		props.put("logging.level.root", "WARN");

		SpringApplication app = new SpringApplication();
		app.setBannerMode(Mode.OFF);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.setAdditionalProfiles(LIQUIBASE_PROFILE);
		app.setSources(new HashSet<>(Arrays.asList(source.getName())));
		app.setDefaultProperties(props);
		app.run();
	}

	@Override
	public void run(String... args) throws Exception {

		LiquibaseDiffGenerator generator = new LiquibaseDiffGenerator();
		generator.setSourceDataSource(primaryDataSource);
		generator.setLiquibaseRunner(liquibaseRunner);
		generator.setSchemaToDiff(schemaToDiff);
		generator.run();
	}
}
