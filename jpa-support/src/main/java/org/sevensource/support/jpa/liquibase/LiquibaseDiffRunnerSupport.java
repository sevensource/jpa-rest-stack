package org.sevensource.support.jpa.liquibase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.diff.output.report.DiffToReport;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Catalog;
import liquibase.structure.core.DatabaseObjectFactory;

@Component
@EnableAutoConfiguration
public abstract class LiquibaseDiffRunnerSupport implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(LiquibaseDiffRunnerSupport.class);
	
	
	
	public static final String LIQUIBASE_PROFILE = "liquibase_diff";
	
	@Autowired
	DataSource primaryDataSource;

	
	static class ReportBuilder {
		private StringBuilder builder;
		ReportBuilder appendLine(String s) {
			builder.append(s).append("\\n");
			return this;
		}
		
		public String asString() {
			return builder.toString();
		}
	}
	
	protected static void start(String[] args, Class<? extends LiquibaseDiffRunnerSupport> source) {
		Map<String, Object> props = new HashMap<>();
		props.put("logging.level.root", "WARN");
		
		SpringApplication app = new SpringApplication();
		app.setBannerMode(Mode.OFF);
		app.setWebEnvironment(false);
		app.setAdditionalProfiles(LIQUIBASE_PROFILE);
		app.setSources(new HashSet<>(Arrays.asList(source)));
		app.setDefaultProperties(props);
		app.run(args);
	}
	
	protected abstract List<Class<? extends LiquibaseRunner>> getLiquibaseRunner();
	protected abstract String getSchemaToDiff();
	

	@Override
	public void run(String... args) throws Exception {
		
		DataSource targetDataSource = createDatabase();
		for(Class<? extends LiquibaseRunner> runnerClazz : getLiquibaseRunner()) {
			migrate(targetDataSource, runnerClazz);
		}
		
		diff(getSchemaToDiff(), primaryDataSource, targetDataSource);
	}
	
	
	public void migrate(DataSource dataSource, Class<? extends LiquibaseRunner> runnerClazz) throws Exception {
		LiquibaseRunner runner = runnerClazz.newInstance();
		runner.setResourceLoader(new DefaultResourceLoader());
		runner.setDataSource(dataSource);
		runner.afterPropertiesSet();
	}
	
	public void diff(String schema, DataSource primaryDataSource, DataSource targetDataSource) throws LiquibaseException, IOException, ParserConfigurationException, SQLException {
		DiffResult result = doDatabaseDiff(schema, primaryDataSource.getConnection(), targetDataSource.getConnection());
		
		ReportBuilder builder = new ReportBuilder();
		builder.appendLine("").appendLine("");
		builder.appendLine(String.format(">> db diff (%s):", schema));
		
		builder.appendLine("=====================");
		
		if(! result.areEqual()) {
			String changeLog = generateChangeLog(result);
			String changeReport = generateChangeReport(result);
        
			builder.appendLine(String.format(">> Report (%s):", schema));
			builder.appendLine(changeReport);
			builder.appendLine(" ");
			builder.appendLine(String.format(">> ChangeLog (%s):", schema));
			builder.appendLine("=======================");
			builder.appendLine(changeLog);
		} else {
			builder.appendLine("No changes");
		}
		
		logger.error("Report: {}", builder.asString());
	}
	
	private DataSource createDatabase() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseName("targetDb");
		factory.setDatabaseType(EmbeddedDatabaseType.H2);
		return factory.getDatabase();
	}
    
	private DiffResult doDatabaseDiff(String schema, Connection referenceConnection, Connection targetConnection) throws LiquibaseException, IOException, ParserConfigurationException {
	    Liquibase liquibase = null;
	    
	    try {
	        Database referenceDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(referenceConnection));
	        Database targetDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(targetConnection));

        	referenceDatabase.setDefaultSchemaName(schema);
        	targetDatabase.setDefaultSchemaName(schema);

	        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
	        liquibase = new Liquibase("", resourceAccessor, referenceDatabase);
	        
	        
	        Set<Class<? extends DatabaseObject>> types = DatabaseObjectFactory.getInstance().getStandardTypes();
	        types.remove(Catalog.class);
	        CompareControl compareControl = new CompareControl(types);
	        
	        return liquibase.diff(referenceDatabase, targetDatabase, compareControl);
	    } finally {
	        if (liquibase != null) {
	            liquibase.forceReleaseLocks();
	        }
	    }
	}
	
	private String generateChangeReport(DiffResult diffResult) throws DatabaseException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        new DiffToReport(diffResult, ps).print();
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}
	
	private String generateChangeLog(DiffResult diffResult) throws DatabaseException, ParserConfigurationException, IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        
        DiffOutputControl diffOutputControl = new DiffOutputControl(false, false, true, null);
        new DiffToChangeLog(diffResult, diffOutputControl).print(ps);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}
}
