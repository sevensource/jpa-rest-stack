package org.sevensource.support.jpa.liquibase.diff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.sevensource.support.jpa.liquibase.LiquibaseRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.Assert;

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

public class LiquibaseDiffGenerator {

	private static final Logger logger = LoggerFactory.getLogger(LiquibaseDiffGenerator.class);
	
	private static final String TARGET_DATABASE_NAME = "targetDb";
	private static final EmbeddedDatabaseType TARGET_DATABASE_TYPE = EmbeddedDatabaseType.H2;
	
	private DataSource sourceDataSource;
	private DataSource targetDataSource;
	private List<Class<? extends LiquibaseRunner>> liquibaseRunner;
	private String schemaToDiff;
	
	public ReportBuilder run() throws Exception {
		if(targetDataSource == null) {
			targetDataSource = createDatabase();
		}
		
		Assert.notNull(sourceDataSource, "sourceDataSource must not be null");
		Assert.notNull(targetDataSource, "targetDataSource must not be null");
		Assert.notNull(schemaToDiff, "schemaToDiff must not be null");
		Assert.notNull(liquibaseRunner, "liquibaseRunner must not be null");
		
		
		for(Class<? extends LiquibaseRunner> runnerClazz : liquibaseRunner) {
			migrate(targetDataSource, runnerClazz);
		}
		
		ReportBuilder report = diff(schemaToDiff, sourceDataSource, targetDataSource);
		final String reportString = report.asString();
		logger.error("Report: {}", reportString);
		return report;
	}
	
	private ReportBuilder diff(String schema, DataSource primaryDataSource, DataSource targetDataSource) throws LiquibaseException, IOException, ParserConfigurationException, SQLException {
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
		

		return builder;
	}
	
	private DiffResult doDatabaseDiff(String schema, Connection referenceConnection, Connection targetConnection) throws LiquibaseException {
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
	
	private DataSource createDatabase() {
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseName(TARGET_DATABASE_NAME);
		factory.setDatabaseType(TARGET_DATABASE_TYPE);
		return factory.getDatabase();
	}
	
	private void migrate(DataSource dataSource, Class<? extends LiquibaseRunner> runnerClazz) throws Exception {
		LiquibaseRunner runner = runnerClazz.newInstance();
		runner.setResourceLoader(new DefaultResourceLoader());
		runner.setDataSource(dataSource);
		runner.afterPropertiesSet();
	}
	
	public void setSourceDataSource(DataSource sourceDataSource) {
		this.sourceDataSource = sourceDataSource;
	}
	
	public void setTargetDataSource(DataSource targetDataSource) {
		this.targetDataSource = targetDataSource;
	}
	
	public void setLiquibaseRunner(List<Class<? extends LiquibaseRunner>> liquibaseRunner) {
		this.liquibaseRunner = liquibaseRunner;
	}
	
	public void setSchemaToDiff(String schemaToDiff) {
		this.schemaToDiff = schemaToDiff;
	}
}
