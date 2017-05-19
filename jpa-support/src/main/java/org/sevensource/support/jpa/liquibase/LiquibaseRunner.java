package org.sevensource.support.jpa.liquibase;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;

public interface LiquibaseRunner extends InitializingBean, BeanNameAware, ResourceLoaderAware {
	public void update() throws Exception;
	public void setDataSource(DataSource dataSource);
}