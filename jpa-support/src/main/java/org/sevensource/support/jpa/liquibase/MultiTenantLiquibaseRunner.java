package org.sevensource.support.jpa.liquibase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import liquibase.integration.spring.MultiTenantSpringLiquibase;

public class MultiTenantLiquibaseRunner extends AbstractLiquibaseRunner<MultiTenantSpringLiquibase> {
	
	private static final Logger logger = LoggerFactory.getLogger(MultiTenantLiquibaseRunner.class);
	
	private DataSource dataSource;
	private final String defaultSchema;
	private final List<String> schemas;
	private final String changeLog;
	
	public MultiTenantLiquibaseRunner(DataSource dataSource, String changeLog, String defaultSchema, List<String> schemas) {
		this.dataSource = dataSource;
		this.changeLog = changeLog;
		this.defaultSchema = defaultSchema;
		this.schemas = schemas;
	}
	
	public MultiTenantLiquibaseRunner(DataSource dataSource, String changeLog, String defaultSchema) {
		this(dataSource, changeLog, defaultSchema, null);
	}
	
	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	private void createSchemas() {
		List<String> schemasToCreate = new ArrayList<>();
		if(StringUtils.hasLength(defaultSchema)) schemasToCreate.add(defaultSchema);
		if(! CollectionUtils.isEmpty(schemas)) schemasToCreate.addAll(schemas);
		
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
		
		List<String> availableSchemas = jdbcTemplate.execute(new ConnectionCallback<List<String>>() {
			@Override
			public List<String> doInConnection(Connection con) throws SQLException, DataAccessException {
				ResultSet rs = con.getMetaData().getSchemas();
				List<String> existing = new ArrayList<>();
				
				while(rs.next()) {
					existing.add( rs.getString("TABLE_SCHEM").toLowerCase());
				}
				rs.close();
				return existing;
			}
		});
		
		for(String schema : schemasToCreate) {
			if(! availableSchemas.contains(schema)) {
				try {
					if (logger.isWarnEnabled()) {
						logger.warn("Creating schema {}", schema);
					}
					final String sql = String.format("CREATE SCHEMA %s", schema);
					jdbcTemplate.execute(sql);
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	@Override
	public void update() throws Exception {
		createSchemas();
		super.update();
	}
	

	@Override
	protected MultiTenantSpringLiquibase init() {
		MultiTenantSpringLiquibase liquibase = new MultiTenantSpringLiquibase();
		liquibase.setDataSource(getDataSource());
		liquibase.setChangeLog(changeLog);
		liquibase.setDefaultSchema(defaultSchema);
		liquibase.setSchemas(schemas);
		liquibase.setShouldRun(true);
		return liquibase;
	}
	
}